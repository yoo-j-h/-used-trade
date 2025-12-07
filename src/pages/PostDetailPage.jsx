import React, { useMemo, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { usePosts } from '../context/PostContext';
import { useUsers } from '../context/UserContext';
import { useComments } from '../context/CommentsContext';
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
} from './PostDetailPage.styled';

const CATEGORIES = ['전자기기', '가구', '생활용품', '의류', '도서', '스포츠', '기타'];
const STATUS = ['판매중', '예약중', '판매완료'];

const PostDetailPage = () => {
  const { postId } = useParams();
  const navigate = useNavigate();

  const numericPostId = Number(postId); // postId는 Date.now()로 만든 숫자라고 가정
  const { getPostById, updatePost } = usePosts();
  const { currentUser } = useUsers();
  const { getCommentsByPostId, addComment, deleteComment } = useComments();

  const post = getPostById(numericPostId);
  const comments = getCommentsByPostId(numericPostId) || [];

  const isOwner = post && currentUser && currentUser.userId === post.sellerId;

  // 게시글 수정용 상태
  const [isEditing, setIsEditing] = useState(false);
  const [editTitle, setEditTitle] = useState(post?.title || '');
  const [editPrice, setEditPrice] = useState(post?.price || '');
  const [editCategory, setEditCategory] = useState(post?.category || CATEGORIES[0]);
  const [editStatus, setEditStatus] = useState(post?.status || '판매중');
  const [editRegion, setEditRegion] = useState(post?.region || '');
  const [editDescription, setEditDescription] = useState(post?.description || '');

  // 최상위 댓글
  const [commentContent, setCommentContent] = useState('');

  // 답글
  const [replyParentId, setReplyParentId] = useState(null);
  const [replyContent, setReplyContent] = useState('');

  // 댓글/답글 트리 구조 만들기
  const commentTree = useMemo(() => {
    const roots = comments.filter((c) => !c.parentId);
    const replyMap = {};
    comments.forEach((c) => {
      if (c.parentId) {
        replyMap[c.parentId] = replyMap[c.parentId] || [];
        replyMap[c.parentId].push(c);
      }
    });
    return { roots, replyMap };
  }, [comments]);

  if (!post) {
    return (
      <PageContainer>
        <EmptyMessage>게시글을 찾을 수 없습니다.</EmptyMessage>
      </PageContainer>
    );
  }

  // 🔹 게시글 전체 수정 저장
  const handleSaveEdit = (e) => {
    e.preventDefault();

    const priceNumber = Number(editPrice);
    if (Number.isNaN(priceNumber) || priceNumber <= 0) {
      alert('가격을 올바르게 입력해 주세요.');
      return;
    }

    updatePost(post.postId, {
      title: editTitle.trim(),
      price: priceNumber,
      category: editCategory,
      status: editStatus,
      region: editRegion.trim(),
      description: editDescription.trim(),
    });

    setIsEditing(false);
    alert('게시글이 수정되었습니다.');
  };

  const handleCancelEdit = () => {
    setEditTitle(post.title);
    setEditPrice(post.price);
    setEditCategory(post.category);
    setEditStatus(post.status);
    setEditRegion(post.region);
    setEditDescription(post.description);
    setIsEditing(false);
  };

  const handleChangeStatus = (nextStatus) => {
    if (!isOwner) return;


    updatePost(post.postId, { status: nextStatus });
  };


  const handleAddRootComment = (e) => {
    e.preventDefault();
    if (!currentUser) {
      alert('댓글을 작성하려면 로그인이 필요합니다.');
      navigate('/login');
      return;
    }
    if (!commentContent.trim()) return;

    addComment({
      postId: post.postId,
      userId: currentUser.userId,
      content: commentContent.trim(),
      parentId: null,
    });

    setCommentContent('');
  };


  const handleAddReply = (e, parentId) => {
    e.preventDefault();
    if (!currentUser) {
      alert('답글을 작성하려면 로그인이 필요합니다.');
      navigate('/login');
      return;
    }
    if (!replyContent.trim()) return;

    addComment({
      postId: post.postId,
      userId: currentUser.userId,
      content: replyContent.trim(),
      parentId,
    });

    setReplyContent('');
    setReplyParentId(null);
  };


  const handleDeleteComment = (commentId, commentUserId) => {
    if (!currentUser || currentUser.userId !== commentUserId) {
      alert('본인이 작성한 댓글만 삭제할 수 있습니다.');
      return;
    }
    if (window.confirm('댓글을 삭제하시겠습니까?')) {
      deleteComment(commentId);
    }
  };

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
                backgroundColor: '#f1f3f5',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#adb5bd',
                fontSize: '12px',
              }}
            >
              이미지 없음
            </MainImage>
          )}
        </ImageBox>

        <InfoBox>
          <Title>{post.title}</Title>
          <Price>{post.price?.toLocaleString?.() ?? post.price}원</Price>

          <MetaRow>
            <RegionText>{post.region || '지역 정보 없음'}</RegionText>
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
            <ButtonRow style={{ marginTop: '12px' }}>
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
            <Select
              value={editCategory}
              onChange={(e) => setEditCategory(e.target.value)}
            >
              {CATEGORIES.map((c) => (
                <option key={c} value={c}>
                  {c}
                </option>
              ))}
            </Select>
            <Select
              value={editStatus}
              onChange={(e) => setEditStatus(e.target.value)}
            >
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
                ? '판매자에게 궁금한 점이나 의견을 남겨보세요.'
                : '댓글을 작성하려면 로그인해 주세요.'
            }
            value={commentContent}
            onChange={(e) => setCommentContent(e.target.value)}
            disabled={!currentUser}
          />
          <ButtonRow style={{ marginTop: '8px' }}>
            <PrimaryButton type="submit" disabled={!currentUser}>
              댓글 등록
            </PrimaryButton>
          </ButtonRow>
        </CommentForm>

        {/* 댓글 + 답글 리스트 */}
        <CommentList>
          {commentTree.roots.length === 0 ? (
            <EmptyMessage>아직 댓글이 없습니다.</EmptyMessage>
          ) : (
            commentTree.roots.map((c) => (
              <div key={c.commentId}>
                <CommentItem>
                  <CommentMeta>
                    <CommentAuthor>{c.userId}</CommentAuthor>
                    <span>
                      {new Date(c.createdAt).toLocaleString('ko-KR')}
                    </span>

                    {currentUser &&
                      currentUser.userId === c.userId && (
                        <CommentDeleteButton
                          type="button"
                          onClick={() =>
                            handleDeleteComment(c.commentId, c.userId)
                          }
                        >
                          삭제
                        </CommentDeleteButton>
                      )}

                    {currentUser && (
                      <ReplyButton
                        type="button"
                        onClick={() => {
                          setReplyParentId((prev) =>
                            prev === c.commentId ? null : c.commentId
                          );
                          setReplyContent('');
                        }}
                      >
                        답글
                      </ReplyButton>
                    )}
                  </CommentMeta>
                  <CommentContent>{c.content}</CommentContent>
                </CommentItem>

                {/* 이 댓글에 달리는 답글 작성 폼 */}
                {replyParentId === c.commentId && (
                  <ReplyForm onSubmit={(e) => handleAddReply(e, c.commentId)}>
                    <ReplyTextarea
                      rows={2}
                      placeholder="답글을 입력해 주세요."
                      value={replyContent}
                      onChange={(e) => setReplyContent(e.target.value)}
                    />
                    <ButtonRow style={{ marginTop: '4px' }}>
                      <SecondaryButton
                        type="button"
                        onClick={() => {
                          setReplyParentId(null);
                          setReplyContent('');
                        }}
                      >
                        취소
                      </SecondaryButton>
                      <PrimaryButton type="submit">등록</PrimaryButton>
                    </ButtonRow>
                  </ReplyForm>
                )}

                {/* 이 댓글의 답글 목록 */}
                {commentTree.replyMap[c.commentId] &&
                  commentTree.replyMap[c.commentId].map((r) => (
                    <CommentItem key={r.commentId} $isReply>
                      <CommentMeta>
                        <CommentAuthor>{r.userId}</CommentAuthor>
                        <span>
                          {new Date(r.createdAt).toLocaleString('ko-KR')}
                        </span>
                        {currentUser &&
                          currentUser.userId === r.userId && (
                            <CommentDeleteButton
                              type="button"
                              onClick={() =>
                                handleDeleteComment(r.commentId, r.userId)
                              }
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
