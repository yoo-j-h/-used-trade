import React, { createContext, useContext, useEffect, useState } from 'react';

const UsersContext = createContext(null);

export const useUsers = () => useContext(UsersContext);


const DB_NAME = 'UdongDB';
const DB_VERSION = 1;
const STORE_NAME = 'users';


const openUsersDB = () => {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open(DB_NAME, DB_VERSION);

    request.onupgradeneeded = (event) => {
      const db = event.target.result;
      if (!db.objectStoreNames.contains(STORE_NAME)) {
        db.createObjectStore(STORE_NAME, { keyPath: 'userId' });
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


const getAllUsersFromDB = (db) => {
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


const putUserToDB = (db, user) => {
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readwrite');
    const store = tx.objectStore(STORE_NAME);
    const req = store.put(user);

    req.onsuccess = () => resolve();
    req.onerror = () => reject(req.error);
  });
};

const deleteUserFromDB = (db, userId) => {
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readwrite');
    const store = tx.objectStore(STORE_NAME);
    const req = store.delete(userId);

    req.onsuccess = () => resolve();
    req.onerror = () => reject(req.error);
  });
};

export const UsersProvider = ({ children }) => {
  const [users, setUsers] = useState([]);
  const [db, setDb] = useState(null);

  const [currentUser, setCurrentUser] = useState(() => {
    const saved = localStorage.getItem('currentUser');
    return saved ? JSON.parse(saved) : null;
  });

  useEffect(() => {
    let cancelled = false;

    openUsersDB()
      .then((dbInstance) => {
        if (cancelled) {
          dbInstance.close();
          return;
        }
        setDb(dbInstance);
        return getAllUsersFromDB(dbInstance);
      })
      .then((initialUsers) => {
        if (!cancelled && initialUsers) {
          setUsers(initialUsers);
        }
      })
      .catch((err) => {
        console.error('IndexedDB 초기화 오류:', err);
      });

    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    if (currentUser) {
      localStorage.setItem('currentUser', JSON.stringify(currentUser));
    } else {
      localStorage.removeItem('currentUser');
    }
  }, [currentUser]);


  const addUser = (user) => {
    setUsers((prev) => [...prev, user]);

    if (db) {
      putUserToDB(db, user).catch((err) =>
        console.error('IndexedDB addUser 오류:', err)
      );
    }
  };


  const updateUser = (userId, updatedData) => {
    setUsers((prev) =>
      prev.map((u) => (u.userId === userId ? { ...u, ...updatedData } : u))
    );

    if (db) {
      const target = users.find((u) => u.userId === userId);
      if (target) {
        const updatedUser = { ...target, ...updatedData };

        putUserToDB(db, updatedUser).catch((err) =>
          console.error('IndexedDB updateUser 오류:', err)
        );
      }
    }

    if (currentUser && currentUser.userId === userId) {
      setCurrentUser((prev) => (prev ? { ...prev, ...updatedData } : prev));
    }
  };

  const changeCredentials = ({
    currentUserId,
    currentPassword,
    newUserId,
    newPassword,
  }) => {
    const user = users.find((u) => u.userId === currentUserId);
    if (!user) {
      return { success: false, message: '사용자를 찾을 수 없습니다.' };
    }


    if (user.password !== currentPassword) {
      return { success: false, message: '현재 비밀번호가 일치하지 않습니다.' };
    }


    const trimmedNewUserId = (newUserId || '').trim();
    const finalUserId = trimmedNewUserId || currentUserId;
    const finalPassword = newPassword ? newPassword : user.password;


    if (
      finalUserId !== currentUserId &&
      users.some((u) => u.userId === finalUserId)
    ) {
      return { success: false, message: '이미 사용 중인 아이디입니다.' };
    }

    const updatedUser = {
      ...user,
      userId: finalUserId,
      password: finalPassword,
    };


    setUsers((prev) =>
      prev.map((u) => (u.userId === currentUserId ? updatedUser : u))
    );

 
    if (db) {
   
      deleteUserFromDB(db, currentUserId)
        .then(() => putUserToDB(db, updatedUser))
        .catch((err) => console.error('IndexedDB changeCredentials 오류:', err));
    }

    if (currentUser && currentUser.userId === currentUserId) {
      setCurrentUser(updatedUser);
    }

    return { success: true, updatedUser };
  };


  const deleteUser = (userId) => {
    setUsers((prev) => prev.filter((u) => u.userId !== userId));

    if (db) {
      deleteUserFromDB(db, userId).catch((err) =>
        console.error('IndexedDB deleteUser 오류:', err)
      );
    }

    
    if (currentUser && currentUser.userId === userId) {
      setCurrentUser(null);
    }
  };

  const getUserById = (userId) => {
    return users.find((u) => u.userId === userId);
  };

  const login = (userId, password) => {
    const user = users.find(
      (u) => u.userId === userId && u.password === password
    );

    if (!user) return null; 

    setCurrentUser(user);
    return user;
  };

  const logout = () => {
    setCurrentUser(null);
  };

  const value = {
    users,
    currentUser,
    addUser,
    updateUser,
    changeCredentials, 
    deleteUser,
    getUserById,
    login,
    logout,
  };

  return (
    <UsersContext.Provider value={value}>{children}</UsersContext.Provider>
  );
};
