import React, { createContext, useContext, useEffect, useState } from "react";
import { http } from "../api/http";

const PostsContext = createContext(null);
export const usePosts = () => useContext(PostsContext);

/**
 * 서버(snake_case) -> 프론트(camelCase)
 */
const toCamelPost = (p) => ({
  boardId: p.board_id,
  title: p.board_title,
  description: p.board_content,
  category: p.category,
  region: p.region,
  price: p.price,
  status: p.sale_status,
  image: p.image_url,
  sellerId: p.user_id ?? p.board_writer,

  createDate: p.create_date,
  modifyDate: p.modify_date,

  // (호환용 alias - 필요없으면 삭제 가능)
  board_id: p.board_id,
  board_title: p.board_title,
  board_content: p.board_content,
  sale_status: p.sale_status,
  image_url: p.image_url,
  user_id: p.user_id ?? p.board_writer,
  create_date: p.create_date,
  modify_date: p.modify_date,
});

/**
 * ✅ 네 폼 데이터 구조 -> 서버 DTO(snake_case)
 * 프론트에서 받는 값:
 * { title, description, category, region, price, status, image, sellerId }
 */
const toSnakeCreatePost = (form) => ({
  board_title: form.title,
  board_content: form.description,
  category: form.category,
  region: form.region,
  price: form.price,
  sale_status: form.status,          // "판매중" 같은 값 그대로
  image_url: form.image ?? null,     // base64 dataURL or null
  user_id: form.sellerId,            // "user01"
});

/**
 * 수정(PATCH)도 같은 입력을 받을 수 있게
 * undefined 제거해서 PATCH 안전하게
 */
const toSnakeUpdatePost = (form) => {
  const payload = {
    board_title: form.title,
    board_content: form.description,
    category: form.category,
    region: form.region,
    price: form.price,
    sale_status: form.status,
    image_url: form.image,
  };

  Object.keys(payload).forEach((k) => payload[k] === undefined && delete payload[k]);
  return payload;
};

export const PostsProvider = ({ children }) => {
  const [posts, setPosts] = useState([]);

  // 목록 로딩
  useEffect(() => {
    http
      .get("/boards")
      .then((res) => {
        const data = res.data?.content ?? res.data;
        setPosts((data ?? []).map(toCamelPost));
      })
      .catch((err) => console.error("boards load failed", err));
  }, []);

  const reload = async () => {
    const res = await http.get("/boards");
    const data = res.data?.content ?? res.data;
    setPosts((data ?? []).map(toCamelPost));
  };

  // ✅ 게시글 생성
  const addPost = async (formData) => {
    const payload = toSnakeCreatePost(formData);
    console.log("POST /boards payload:", payload);

    const res = await http.post("/boards", payload);
    const boardId = res.data;

    await reload();
    return boardId;
  };

  const getPostById = async (boardId) => {
    const res = await http.get(`/boards/${boardId}`);
    return toCamelPost(res.data);
  };

  // ✅ 게시글 수정
  const updatePost = async (boardId, formData) => {
    const payload = toSnakeUpdatePost(formData);
    console.log(`PATCH /boards/${boardId} payload:`, payload);

    const res = await http.patch(`/boards/${boardId}`, payload);
    await reload();
    return res.data ? toCamelPost(res.data) : null;
  };

  // ✅ 게시글 삭제
  const deletePost = async (boardId) => {
    await http.delete(`/boards/${boardId}`);
    setPosts((prev) => prev.filter((p) => p.boardId !== boardId));
  };

  const value = {
    posts, // 프론트에선 title/description/sellerId/status/image 로 쓸 수 있음
    addPost,
    updatePost,
    deletePost,
    getPostById,
    reload,
  };

  return <PostsContext.Provider value={value}>{children}</PostsContext.Provider>;
};
