// src/pages/PostCard.jsx
import React from "react";
import { useNavigate } from "react-router-dom";
import {
  CardContainer,
  ThumbnailWrapper,
  Thumbnail,
  InfoContainer,
  Title,
  Price,
  MetaRow,
  RegionText,
  CategoryTag,
  StatusBadge,
  StatusOverlay,
} from "./PostCard.styled";

const PostCard = ({ post }) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/posts/${post.boardId}`); // ✅ boardId
  };

  const showOverlay = post.status === "예약중" || post.status === "판매완료";

  return (
    <CardContainer onClick={handleClick}>
      <ThumbnailWrapper>
        {post.image ? (
          <Thumbnail src={post.image} alt={post.title} />
        ) : (
          <Thumbnail
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
          </Thumbnail>
        )}

        {showOverlay && (
          <StatusOverlay $status={post.status}>{post.status}</StatusOverlay>
        )}
      </ThumbnailWrapper>

      <InfoContainer>
        <Title>{post.title}</Title>
        <Price>{post.price?.toLocaleString?.() ?? post.price}원</Price>

        <MetaRow>
          <RegionText>{post.region || "지역 정보 없음"}</RegionText>
          <CategoryTag>{post.category}</CategoryTag>
        </MetaRow>

        <MetaRow>
          <StatusBadge $status={post.status}>{post.status}</StatusBadge>
        </MetaRow>
      </InfoContainer>
    </CardContainer>
  );
};

export default PostCard;
