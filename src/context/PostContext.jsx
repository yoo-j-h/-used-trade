import React, { createContext, useContext, useEffect, useState } from 'react';

const PostsContext = createContext(null);

export const usePosts = () => useContext(PostsContext);

const DB_NAME = 'UdongPostsDB';
const STORE_NAME = 'posts';


const openPostsDB = () => {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open(DB_NAME, DB_VERSION);

    request.onupgradeneeded = (event) => {
      const db = event.target.result;
      if (!db.objectStoreNames.contains(STORE_NAME)) {
    
        db.createObjectStore(STORE_NAME, { keyPath: 'postId' });
      }
    };

    request.onsuccess = (event) => {
      resolve(event.target.result);
    };

    request.onerror = (event) => {
      reject(event.target.error);
    };
  });
};


const getAllPostsFromDB = (db) => {
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readonly');
    const store = tx.objectStore(STORE_NAME);
    const req = store.getAll();

    req.onsuccess = () => {
      resolve(req.result || []);
    };

    req.onerror = () => {
      reject(req.error);
    };
  });
};


const putPostToDB = (db, post) => {
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readwrite');
    const store = tx.objectStore(STORE_NAME);
    const req = store.put(post);

    req.onsuccess = () => resolve();
    req.onerror = () => reject(req.error);
  });
};


const deletePostFromDB = (db, postId) => {
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readwrite');
    const store = tx.objectStore(STORE_NAME);
    const req = store.delete(postId);

    req.onsuccess = () => resolve();
    req.onerror = () => reject(req.error);
  });
};

export const PostsProvider = ({ children }) => {
  const [posts, setPosts] = useState([]);
  const [db, setDb] = useState(null);

  useEffect(() => {
    let cancelled = false;

    openPostsDB()
      .then((dbInstance) => {
        if (cancelled) {
          dbInstance.close();
          return;
        }
        console.log('📁 Posts DB 열림');
        setDb(dbInstance);
        return getAllPostsFromDB(dbInstance);
      })
      .then((initialPosts) => {
        if (!cancelled && initialPosts) {
          console.log('📄 초기 로드 posts:', initialPosts);
          setPosts(initialPosts);
        }
      })
      .catch((err) => {
        console.error('IndexedDB(Posts) 초기화 오류:', err);
      });

    return () => {
      cancelled = true;
    };
  }, []);


  const addPost = (post) => {
    const newPost = {
      ...post,
      postId: post.postId || Date.now(),
      createdAt: post.createdAt || new Date().toISOString(),
    };

    console.log('✅ addPost 호출, newPost:', newPost);

    setPosts((prev) => [...prev, newPost]);

    if (db) {
      putPostToDB(db, newPost).catch((err) =>
        console.error('IndexedDB addPost 오류:', err)
      );
    }
  };


  const updatePost = (postId, updatedData) => {
    setPosts((prev) => {
      const target = prev.find((p) => p.postId === postId);
      if (!target) return prev;

      const updatedPost = { ...target, ...updatedData };

      if (db) {
        putPostToDB(db, updatedPost).catch((err) =>
          console.error('IndexedDB updatePost 오류:', err)
        );
      }

      return prev.map((p) => (p.postId === postId ? updatedPost : p));
    });
  };


  const deletePost = (postId) => {
    console.log('🗑 deletePost 호출, postId:', postId);
    setPosts((prev) => prev.filter((p) => p.postId !== postId));

    if (db) {
      deletePostFromDB(db, postId).catch((err) =>
        console.error('IndexedDB deletePost 오류:', err)
      );
    }
  };

  const getPostById = (postId) => {
    return posts.find((p) => p.postId === postId);
  };

  const value = {
    posts,
    addPost,
    updatePost,
    deletePost,
    getPostById,
  };

  return (
    <PostsContext.Provider value={value}>
      {children}
    </PostsContext.Provider>
  );
};
