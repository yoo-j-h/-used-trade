import React, { createContext,useContext, useState } from 'react'

const UsersContext = createContext(null);

export const UsersProvider = ({children}) =>{
    const [users, setUsers] = useState([]);

    const addUser = (user) =>{
        setUsers((prev) => [
            ...prev,
            user
        ]);
    };

    const deleteUser = (userId) =>{
        setUsers((prev) => prev.filter((u) => u.userId !== userId));
    };

    const getUserById = (userId) => {
        return users.find((u) => u.userId === userId);
    };
  return (
    <UsersContext.Provider value={{ users, addUser, deleteUser, getUserById }}>
      {children}
    </UsersContext.Provider>
  );
};

export const useUsers = () => useContext(UsersContext);