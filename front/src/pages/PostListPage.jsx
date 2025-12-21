import React, { useState, useMemo } from 'react';
import { usePosts } from '../context/PostContext';
import PostCard from './PostCard';
import {
  PageContainer,
  HeaderRow,
  TitleText,
  NewPostButton,
  CategoryFilterBar,
  CategoryButton,
  CardsGrid,
  EmptyMessage,
} from './PostListPage.styled';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '../routes/routePaths';
import { useUsers } from '../context/UserContext';

const CATEGORIES = ['전체', '전자기기', '가구', '생활용품', '의류', '도서', '스포츠', '기타'];

const PostListPage = () => {
  const navigate = useNavigate();
  const { posts } = usePosts();

  const [selectedCategory, setSelectedCategory] = useState('전체');

  const filteredPosts = useMemo(() => {
    if (selectedCategory === '전체') return posts;

    return posts.filter((p) => p.category === selectedCategory);
  }, [posts, selectedCategory]);

  const handleChangeCategory = (category) => {
    setSelectedCategory(category);
  };

  const handleClickNewPost = () => {
    navigate(ROUTES.POST); 
  };
  const {currentUser} = useUsers();
  
  return (
    <PageContainer>
   
      <HeaderRow>
        <TitleText>우동 마켓</TitleText>
        {currentUser && (
        <NewPostButton type="button" onClick={handleClickNewPost}>
          + 새 게시글
        </NewPostButton>
        )}
      </HeaderRow>

      <CategoryFilterBar>
        {CATEGORIES.map((category) => (
          <CategoryButton
            key={category}
            type="button"
            $active={selectedCategory === category}
            onClick={() => handleChangeCategory(category)}
          >
            {category}
          </CategoryButton>
        ))}
      </CategoryFilterBar>


      {filteredPosts.length === 0 ? (
        <EmptyMessage>해당 카테고리에 게시글이 없습니다.</EmptyMessage>
      ) : (
        <CardsGrid>
          {filteredPosts.map((post) => (
            <PostCard key={post.postId} post={post} />
          ))}
        </CardsGrid>
      )}
    </PageContainer>
  );
};

export default PostListPage;
