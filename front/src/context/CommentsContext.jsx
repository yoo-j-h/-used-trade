// src/context/CommentsContext.jsx
import React, { createContext, useContext, useCallback, useMemo, useState } from "react";
import { http } from "../api/http";

const CommentsContext = createContext(null);
export const useComments = () => useContext(CommentsContext);

const toCamelReply = (r, boardId) => ({
  commentId: r.reply_no,
  postId: r.board_id ?? boardId,
  userId: r.reply_writer,
  content: r.reply_content,
  parentId: r.parent_id ?? null,
  createdAt: r.create_date,
  updatedAt: r.modify_date,
});

const toSnakeCreate = ({ userId, content, parentId }) => ({
  reply_writer: userId,
  reply_content: content,
  parent_id: parentId ?? null,
});

export const CommentsProvider = ({ children }) => {
  const [byBoardId, setByBoardId] = useState({});

  const loadComments = useCallback(async (boardId) => {
    if (!Number.isFinite(boardId) || boardId <= 0) return;

    const res = await http.get(`/boards/${boardId}/replies`);
    const arr = Array.isArray(res.data) ? res.data : [];

    const mapped = arr.map((r) => toCamelReply(r, boardId));
    setByBoardId((prev) => ({ ...prev, [boardId]: mapped }));
  }, []);

  const getCommentsByPostId = useCallback(
    (boardId) => {
      const arr = byBoardId[boardId];
      return Array.isArray(arr) ? arr : [];
    },
    [byBoardId]
  );

  const addComment = useCallback(
    async ({ postId, userId, content, parentId }) => {
      const payload = toSnakeCreate({ userId, content, parentId });
      console.log("POST /boards/:id/replies payload:", payload);

      try {
        await http.post(`/boards/${postId}/replies`, payload);
        await loadComments(postId);
      } catch (e) {
        console.error("addComment failed", e?.response?.data ?? e);
        throw e;
      }
    },
    [loadComments]
  );

  const deleteComment = useCallback(
    async (commentId, postId) => {
      await http.delete(`/replies/${commentId}`);
      await loadComments(postId);
    },
    [loadComments]
  );

  const value = useMemo(
    () => ({
      loadComments,
      getCommentsByPostId,
      addComment,
      deleteComment,
    }),
    [loadComments, getCommentsByPostId, addComment, deleteComment]
  );

  return <CommentsContext.Provider value={value}>{children}</CommentsContext.Provider>;
};
