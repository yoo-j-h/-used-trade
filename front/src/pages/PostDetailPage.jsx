// src/pages/PostDetailPage.jsx
import React, { useEffect, useMemo, useState, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { usePosts } from "../context/PostsContext";
import { useUsers } from "../context/UsersContext";
import { useComments } from "../context/CommentsContext";
import {
  PageContainer,
  TopLayout,
  ImageBox,
  MainImage,
  InfoBox,
  Title,
  Price,
  MetaRow,
  RegionText,
  CategoryTag,
  StatusBadge,
  Description,
  SectionTitle,
  EditForm,
  Input,
  Select,
  TextArea,
  ButtonRow,
  PrimaryButton,
  SecondaryButton,
  CommentSection,
  CommentForm,
  CommentTextarea,
  CommentList,
  CommentItem,
  CommentMeta,
  CommentAuthor,
  CommentContent,
  CommentDeleteButton,
  ReplyForm,
  ReplyTextarea,
  ReplyButton,
  StatusButtonsWrapper,
  StatusChangeButton,
  EmptyMessage,
} from "./PostDetailPage.styled";

const CATEGORIES = ["전자기기", "가구", "생활용품", "의류", "도서", "스포츠", "기타"];
const STATUS = ["판매중", "예약중", "판매완료"];

const PostDetailPage = () => {
  const { postId } = useParams(); // 라우트: /posts/:postId
  const navigate = useNavigate();

  const boardId = Number(postId);

  const { getPostById, updatePost } = usePosts();
  const { currentUser, getUserById } = useUsers();
  const { loadComments, getCommentsByPostId, addComment, deleteComment } = useComments();

  // ✅ 게시글 state (getPostById가 async라면)
  const [post, setPost] = useState(null);
  const [loadingPost, setLoadingPost] = useState(true);

  // ✅ 댓글 입력
  const [commentContent, setCommentContent] = useState("");

  // ✅ 답글 입력
  const [replyParentId, setReplyParentId] = useState(null);
  const [replyContent, setReplyContent] = useState("");

  // ✅ 수정 폼
  const [isEditing, setIsEditing] = useState(false);
  const [editTitle, setEditTitle] = useState("");
  const [editPrice, setEditPrice] = useState("");
  const [editCategory, setEditCategory] = useState(CATEGORIES[0]);
  const [editStatus, setEditStatus] = useState("판매중");
  const [editRegion, setEditRegion] = useState("");
  const [editDescription, setEditDescription] = useState("");

  // =========================
  // 1) 게시글 로딩
  // =========================
  useEffect(() => {
    const run = async () => {
      if (!Number.isFinite(boardId) || boardId <= 0) {
        setLoadingPost(false);
        setPost(null);
        return;
      }

      try {
        setLoadingPost(true);
        const data = await getPostById(boardId);
        setPost(data ?? null);
      } catch (e) {
        console.error("getPostById failed", e);
        setPost(null);
      } finally {
        setLoadingPost(false);
      }
    };

    run();
  }, [boardId, getPostById]);

  // 게시글 로딩 후 수정폼 초기화
  useEffect(() => {
    if (!post) return;
    setEditTitle(post.title ?? "");
    setEditPrice(post.price ?? "");
    setEditCategory(post.category ?? CATEGORIES[0]);
    setEditStatus(post.status ?? "판매중");
    setEditRegion(post.region ?? "");
    setEditDescription(post.description ?? "");
  }, [post]);

  // =========================
  // 2) 댓글 로딩 (🔥 무한요청 방지 핵심)
  // =========================
  useEffect(() => {
    if (!Number.isFinite(boardId) || boardId <= 0) return;

    // ✅ 여기서만 서버 호출
    loadComments(boardId).catch((e) => {
      console.error("loadComments failed", e?.response?.data ?? e);
    });
  }, [boardId, loadComments]);

  // 렌더에서는 "읽기만"
  const comments = getCommentsByPostId(boardId);

  // seller / owner
  const seller = post ? getUserById?.(post.sellerId) : null;
  const isOwner = post && currentUser && currentUser.userId === post.sellerId;

  // 댓글 트리 구성
  const commentTree = useMemo(() => {
    const safe = Array.isArray(comments) ? comments : [];
    const roots = safe.filter((c) => !c.parentId);
    const replyMap = {};
    safe.forEach((c) => {
      if (c.parentId) {
        replyMap[c.parentId] = replyMap[c.parentId] || [];
        replyMap[c.parentId].push(c);
      }
    });
    return { roots, replyMap };
  }, [comments]);

  // =========================
  // UI 상태 처리
  // =========================
  if (loadingPost) {
    return (
      <PageContainer>
        <EmptyMessage>불러오는 중...</EmptyMessage>
      </PageContainer>
    );
  }

  if (!post) {
    return (
      <PageContainer>
        <EmptyMessage>게시글을 찾을 수 없습니다.</EmptyMessage>
      </PageContainer>
    );
  }

  // =========================
  // 핸들러들
  // =========================
  const handleSaveEdit = async (e) => {
    e.preventDefault();

    const priceNumber = Number(editPrice);
    if (Number.isNaN(priceNumber) || priceNumber < 0) {
      alert("가격을 올바르게 입력해 주세요.");
      return;
    }

    await updatePost(post.boardId, {
      title: editTitle.trim(),
      price: priceNumber,
      category: editCategory,
      status: editStatus,
      region: editRegion.trim(),
      description: editDescription.trim(),
    });

    // 갱신
    const refreshed = await getPostById(post.boardId);
    setPost(refreshed);

    setIsEditing(false);
    alert("게시글이 수정되었습니다.");
  };

  const handleCancelEdit = () => {
    setEditTitle(post.title ?? "");
    setEditPrice(post.price ?? "");
    setEditCategory(post.category ?? CATEGORIES[0]);
    setEditStatus(post.status ?? "판매중");
    setEditRegion(post.region ?? "");
    setEditDescription(post.description ?? "");
    setIsEditing(false);
  };

  const handleChangeStatus = async (nextStatus) => {
    if (!isOwner) return;

    await updatePost(post.boardId, { status: nextStatus });
    const refreshed = await getPostById(post.boardId);
    setPost(refreshed);
  };

  const handleAddRootComment = async (e) => {
    e.preventDefault();

    if (!currentUser) {
      alert("댓글을 작성하려면 로그인이 필요합니다.");
      navigate("/login");
      return;
    }
    if (!commentContent.trim()) return;

    try {
      await addComment({
        postId: post.boardId,
        userId: currentUser.userId,
        content: commentContent.trim(),
        parentId: null,
      });
      setCommentContent("");
    } catch (e2) {
      // 서버에서 "회원 없음"이면 여기로 떨어짐
      alert(e2?.response?.data?.message ?? "댓글 등록에 실패했습니다.");
    }
  };

  const handleAddReply = async (e, parentId) => {
    e.preventDefault();

    if (!currentUser) {
      alert("답글을 작성하려면 로그인이 필요합니다.");
      navigate("/login");
      return;
    }
    if (!replyContent.trim()) return;

    try {
      await addComment({
        postId: post.boardId,
        userId: currentUser.userId,
        content: replyContent.trim(),
        parentId,
      });
      setReplyContent("");
      setReplyParentId(null);
    } catch (e2) {
      alert(e2?.response?.data?.message ?? "답글 등록에 실패했습니다.");
    }
  };

  const handleDeleteComment = async (commentId, commentUserId) => {
    if (!currentUser || currentUser.userId !== commentUserId) {
      alert("본인이 작성한 댓글만 삭제할 수 있습니다.");
      return;
    }

    if (!window.confirm("댓글을 삭제하시겠습니까?")) return;

    try {
      await deleteComment(commentId, post.boardId);
    } catch (e) {
      alert("댓글 삭제에 실패했습니다.");
    }
  };

  // =========================
  // Render
  // =========================
  return (
    <PageContainer>
      <TopLayout>
        <ImageBox>
          {post.image ? (
            <MainImage src={post.image} alt={post.title} />
          ) : (
            <MainImage
              as="div"
              style={{
                backgroundColor: "#f1f3f5",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                color: "#adb5bd",
                fontSize: "12px",
              }}
            >
              이미지 없음
            </MainImage>
          )}
        </ImageBox>

        <InfoBox>
          <Title>{post.title}</Title>

          <MetaRow>
            <RegionText>게시자: {seller?.userId ?? post.sellerId ?? "알 수 없음"}</RegionText>
          </MetaRow>

          <Price>{post.price?.toLocaleString?.() ?? post.price}원</Price>

          <MetaRow>
            <RegionText>{post.region || "지역 정보 없음"}</RegionText>
            <CategoryTag>{post.category}</CategoryTag>
          </MetaRow>

          <MetaRow>
            <StatusBadge $status={post.status}>{post.status}</StatusBadge>

            {isOwner && (
              <StatusButtonsWrapper>
                {STATUS.map((s) => (
                  <StatusChangeButton
                    key={s}
                    type="button"
                    $active={post.status === s}
                    onClick={() => handleChangeStatus(s)}
                  >
                    {s}
                  </StatusChangeButton>
                ))}
              </StatusButtonsWrapper>
            )}
          </MetaRow>

          <Description>{post.description}</Description>

          {isOwner && (
            <ButtonRow style={{ marginTop: "12px" }}>
              <PrimaryButton type="button" onClick={() => setIsEditing(true)}>
                게시글 수정하기
              </PrimaryButton>
            </ButtonRow>
          )}
        </InfoBox>
      </TopLayout>

      {isOwner && isEditing && (
        <>
          <SectionTitle>게시글 수정</SectionTitle>
          <EditForm onSubmit={handleSaveEdit}>
            <Input
              type="text"
              placeholder="제목"
              value={editTitle}
              onChange={(e) => setEditTitle(e.target.value)}
            />
            <Input
              type="number"
              min="0"
              placeholder="가격"
              value={editPrice}
              onChange={(e) => setEditPrice(e.target.value)}
            />

            <Select value={editCategory} onChange={(e) => setEditCategory(e.target.value)}>
              {CATEGORIES.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </Select>

            <Select value={editStatus} onChange={(e) => setEditStatus(e.target.value)}>
              {STATUS.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </Select>

            <Input
              type="text"
              placeholder="거래 지역"
              value={editRegion}
              onChange={(e) => setEditRegion(e.target.value)}
            />

            <TextArea
              rows={4}
              placeholder="상세 설명"
              value={editDescription}
              onChange={(e) => setEditDescription(e.target.value)}
            />

            <ButtonRow>
              <SecondaryButton type="button" onClick={handleCancelEdit}>
                취소
              </SecondaryButton>
              <PrimaryButton type="submit">저장하기</PrimaryButton>
            </ButtonRow>
          </EditForm>
        </>
      )}

      <SectionTitle>댓글</SectionTitle>
      <CommentSection>
        <CommentForm onSubmit={handleAddRootComment}>
          <CommentTextarea
            rows={3}
            placeholder={
              currentUser
                ? "판매자에게 궁금한 점이나 의견을 남겨보세요."
                : "댓글을 작성하려면 로그인해 주세요."
            }
            value={commentContent}
            onChange={(e) => setCommentContent(e.target.value)}
            disabled={!currentUser}
          />
          <ButtonRow style={{ marginTop: "8px" }}>
            <PrimaryButton type="submit" disabled={!currentUser}>
              댓글 등록
            </PrimaryButton>
          </ButtonRow>
        </CommentForm>

        <CommentList>
          {commentTree.roots.length === 0 ? (
            <EmptyMessage>아직 댓글이 없습니다.</EmptyMessage>
          ) : (
            commentTree.roots.map((c) => (
              <div key={c.commentId}>
                <CommentItem>
                  <CommentMeta>
                    <CommentAuthor>{c.userId}</CommentAuthor>
                    <span>{new Date(c.createdAt).toLocaleString("ko-KR")}</span>

                    {currentUser && currentUser.userId === c.userId && (
                      <CommentDeleteButton
                        type="button"
                        onClick={() => handleDeleteComment(c.commentId, c.userId)}
                      >
                        삭제
                      </CommentDeleteButton>
                    )}

                    {currentUser && (
                      <ReplyButton
                        type="button"
                        onClick={() => {
                          setReplyParentId((prev) => (prev === c.commentId ? null : c.commentId));
                          setReplyContent("");
                        }}
                      >
                        답글
                      </ReplyButton>
                    )}
                  </CommentMeta>
                  <CommentContent>{c.content}</CommentContent>
                </CommentItem>

                {replyParentId === c.commentId && (
                  <ReplyForm onSubmit={(e) => handleAddReply(e, c.commentId)}>
                    <ReplyTextarea
                      rows={2}
                      placeholder="답글을 입력해 주세요."
                      value={replyContent}
                      onChange={(e) => setReplyContent(e.target.value)}
                    />
                    <ButtonRow style={{ marginTop: "4px" }}>
                      <SecondaryButton
                        type="button"
                        onClick={() => {
                          setReplyParentId(null);
                          setReplyContent("");
                        }}
                      >
                        취소
                      </SecondaryButton>
                      <PrimaryButton type="submit">등록</PrimaryButton>
                    </ButtonRow>
                  </ReplyForm>
                )}

                {commentTree.replyMap[c.commentId]?.map((r) => (
                  <CommentItem key={r.commentId} $isReply>
                    <CommentMeta>
                      <CommentAuthor>{r.userId}</CommentAuthor>
                      <span>{new Date(r.createdAt).toLocaleString("ko-KR")}</span>

                      {currentUser && currentUser.userId === r.userId && (
                        <CommentDeleteButton
                          type="button"
                          onClick={() => handleDeleteComment(r.commentId, r.userId)}
                        >
                          삭제
                        </CommentDeleteButton>
                      )}
                    </CommentMeta>
                    <CommentContent>{r.content}</CommentContent>
                  </CommentItem>
                ))}
              </div>
            ))
          )}
        </CommentList>
      </CommentSection>
    </PageContainer>
  );
};

export default PostDetailPage;
