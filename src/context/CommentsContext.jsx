import React, { createContext, useContext, useEffect, useState } from 'react';

const CommentsContext = createContext(null);

export const useComments = () => useContext(CommentsContext);


const DB_NAME = 'UdongCommentsDB';
const DB_VERSION = 1;
const STORE_NAME = 'comments';


const openCommentsDB = () => {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open(DB_NAME, DB_VERSION);

    request.onupgradeneeded = (event) => {
      const db = event.target.result;
      if (!db.objectStoreNames.contains(STORE_NAME)) {
        db.createObjectStore(STORE_NAME, { keyPath: 'commentId' });
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

const getAllCommentsFromDB = (db) => {
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

const putCommentToDB = (db, comment) => {
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readwrite');
    const store = tx.objectStore(STORE_NAME);
    const req = store.put(comment);

    req.onsuccess = () => resolve();
    req.onerror = () => reject(req.error);
  });
};


const deleteCommentFromDB = (db, commentId) => {
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readwrite');
    const store = tx.objectStore(STORE_NAME);
    const req = store.delete(commentId);

    req.onsuccess = () => resolve();
    req.onerror = () => reject(req.error);
  });
};

export const CommentsProvider = ({ children }) => {
  const [comments, setComments] = useState([]);
  const [db, setDb] = useState(null);

  
  useEffect(() => {
    let cancelled = false;

    openCommentsDB()
      .then((dbInstance) => {
        if (cancelled) {
          dbInstance.close();
          return;
        }
        setDb(dbInstance);
        return getAllCommentsFromDB(dbInstance);
      })
      .then((initialComments) => {
        if (!cancelled && initialComments) {
          setComments(initialComments);
        }
      })
      .catch((err) => {
        console.error('IndexedDB(Comments) 초기화 오류:', err);
      });

    return () => {
      cancelled = true;
    };
  }, []);


  const addComment = (data) => {
    const newComment = {
      ...data,
      commentId: Date.now(), 
      parentId: data.parentId || null, 
      createdAt: new Date().toISOString(),
    };

    setComments((prev) => [...prev, newComment]);

    if (db) {
      putCommentToDB(db, newComment).catch((err) =>
        console.error('IndexedDB addComment 오류:', err)
      );
    }
  };

  const deleteComment = (commentId) => {
    setComments((prev) => prev.filter((c) => c.commentId !== commentId));

    if (db) {
      deleteCommentFromDB(db, commentId).catch((err) =>
        console.error('IndexedDB deleteComment 오류:', err)
      );
    }
  };

  
  const getCommentsByPostId = (postId) => {
    return comments
      .filter((c) => c.postId === postId)
      .sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
  };

  const value = {
    comments,
    addComment,
    deleteComment,
    getCommentsByPostId,
  };

  return (
    <CommentsContext.Provider value={value}>
      {children}
    </CommentsContext.Provider>
  );
};
