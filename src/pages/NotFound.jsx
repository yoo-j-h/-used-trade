import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Wrapper,
  Title,
  Message,
  HomeButton
} from './NotFound.styled';

const NotFound = () => {
  const navigate = useNavigate();

  return (
    <Wrapper>
      <Title>404</Title>
      <Message>페이지를 찾을 수 없습니다.</Message>

      <HomeButton onClick={() => navigate('/')}>
        홈으로 돌아가기
      </HomeButton>
    </Wrapper>
  );
};

export default NotFound;
