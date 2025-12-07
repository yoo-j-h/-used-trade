import React, { createContext, useContext, useEffect, useState } from 'react';

const CommentsContext = createContext(null);

export const useComments = () => useContext(CommentsContext);

// ---------- IndexedDB 헬퍼 ----------
const DB_NAME = 'UdongCommentsDB';
const DB_VERSION = 1;
const STORE_NAME = 'comments';

// DB 열기
const openCommentsDB = () => {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open(DB_NAME, DB_VERSION);

    request.onupgradeneeded = (event) => {
      const db = event.target.result;
      if (!db.objectStoreNames.contains(STORE_NAME)) {
        // commentId 기본키
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

// 전체 댓글 가져오기
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

// 댓글 추가/업데이트
const putCommentToDB = (db, comment) => {
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readwrite');
    const store = tx.objectStore(STORE_NAME);
    const req = store.put(comment);

    req.onsuccess = () => resolve();
    req.onerror = () => reject(req.error);
  });
};

// 댓글 삭제
const deleteCommentFromDB = (db, commentId) => {
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readwrite');
    const store = tx.objectStore(STORE_NAME);
    const req = store.delete(commentId);

    req.onsuccess = () => resolve();
    req.onerror = () => reject(req.error);
  });
};

// ---------- CommentsProvider ----------
export const CommentsProvider = ({ children }) => {
  const [comments, setComments] = useState([]);
  const [db, setDb] = useState(null);

  // 앱 시작 시 DB 열고 댓글 목록 불러오기
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

  // 🔹 댓글 추가 (일반 댓글 + 답글 공통)
  // data: { postId, userId, content, parentId? }
  const addComment = (data) => {
    const newComment = {
      ...data,
      commentId: Date.now(), // 간단한 id
      parentId: data.parentId || null, // null이면 최상위 댓글
      createdAt: new Date().toISOString(),
    };

    setComments((prev) => [...prev, newComment]);

    if (db) {
      putCommentToDB(db, newComment).catch((err) =>
        console.error('IndexedDB addComment 오류:', err)
      );
    }
  };

  // 🔹 댓글 삭제 (간단히 해당 댓글만 삭제)
  const deleteComment = (commentId) => {
    setComments((prev) => prev.filter((c) => c.commentId !== commentId));

    if (db) {
      deleteCommentFromDB(db, commentId).catch((err) =>
        console.error('IndexedDB deleteComment 오류:', err)
      );
    }
  };

  // 🔹 특정 게시글의 댓글들 가져오기
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
