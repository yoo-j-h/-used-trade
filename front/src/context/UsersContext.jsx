import React, { createContext, useContext, useEffect, useState } from "react";
import { http } from "../api/http";

const UsersContext = createContext(null);
export const useUsers = () => useContext(UsersContext);

/**
 * ===============================
 * 서버(snake_case) → 프론트(camelCase)
 * + name alias 제공
 * ===============================
 */
const toCamelUser = (u) => ({
  userId: u.user_id,

  // 공식 필드
  userName: u.user_name,

  // ✅ alias (기존 코드 호환용)
  name: u.user_name,

  email: u.email,
  phone: u.phone,
  address: u.address,
  createDate: u.create_date,
  modifyDate: u.modify_date,
});

/**
 * ===============================
 * 프론트(camelCase) → 서버(snake_case)
 * (회원가입)
 * ===============================
 */
const toSnakeCreateUser = (u) => ({
  user_id: u.userId,
  user_pwd: u.userPwd ?? u.password,
  user_name: u.userName ?? u.name,
  email: u.email ?? null,
  phone: u.phone,
  address: u.address,
});

/**
 * ===============================
 * 프론트(camelCase) → 서버(snake_case)
 * (회원수정)
 * ===============================
 */
const toSnakeUpdateUser = (u) => ({
  user_name: u.userName ?? u.name,
  email: u.email ?? null,
  phone: u.phone,
  address: u.address,
});

export const UsersProvider = ({ children }) => {
  const [users, setUsers] = useState([]);

  const [currentUser, setCurrentUser] = useState(() => {
    const saved = localStorage.getItem("currentUser");
    return saved ? JSON.parse(saved) : null;
  });

  /**
   * ===============================
   * 회원 전체 조회
   * ===============================
   */
  useEffect(() => {
    http
      .get("/members")
      .then((res) => setUsers(res.data.map(toCamelUser)))
      .catch((err) => console.error("members load failed", err));
  }, []);

  /**
   * ===============================
   * 로그인 상태 localStorage 동기화
   * ===============================
   */
  useEffect(() => {
    if (currentUser) {
      localStorage.setItem("currentUser", JSON.stringify(currentUser));
    } else {
      localStorage.removeItem("currentUser");
    }
  }, [currentUser]);

  /**
   * ===============================
   * 회원가입
   * ===============================
   */
  const addUser = async (user) => {
    const payload = toSnakeCreateUser(user);
    console.log("POST /members payload:", payload);

    await http.post("/members", payload);

    const list = await http.get("/members");
    setUsers(list.data.map(toCamelUser));
  };

  /**
   * ===============================
   * 회원 수정
   * ===============================
   */
  const updateUser = async (userId, updatedData) => {
    const payload = toSnakeUpdateUser(updatedData);
    const res = await http.put(`/members/${userId}`, payload);

    const updatedUser = toCamelUser(res.data);

    setUsers((prev) =>
      prev.map((u) => (u.userId === userId ? updatedUser : u))
    );

    if (currentUser?.userId === userId) {
      setCurrentUser(updatedUser);
    }

    return updatedUser;
  };

  /**
   * ===============================
   * 회원 삭제
   * ===============================
   */
  const deleteUser = async (userId) => {
    await http.delete(`/members/${userId}`);

    setUsers((prev) => prev.filter((u) => u.userId !== userId));

    if (currentUser?.userId === userId) {
      setCurrentUser(null);
    }
  };

  /**
   * ===============================
   * 로그인
   * ===============================
   */
  const login = async (userId, userPwd) => {
    const res = await http.post("/members/login", {
      user_id: userId,
      user_pwd: userPwd,
    });

    const loggedInUser = toCamelUser(res.data);
    setCurrentUser(loggedInUser);
    return loggedInUser;
  };

  const logout = () => setCurrentUser(null);

  const value = {
    users,        // camelCase
    currentUser,  // camelCase + name alias
    addUser,
    updateUser,
    deleteUser,
    login,
    logout,
  };

  return (
    <UsersContext.Provider value={value}>
      {children}
    </UsersContext.Provider>
  );
};
