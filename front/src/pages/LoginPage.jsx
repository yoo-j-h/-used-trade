import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  HorizontalRule,
  InputBar,
  InputBox,
  InputButton,
  InputContainer,
  InputMainText,
  InputPage,
  LeftAlign,
  TextLinks,
} from './Input.styled';
import { useUsers } from '../context/UserContext';
import { ROUTES } from '../routes/routePaths';

const LoginPage = () => {
  const navigate = useNavigate();

  const { login } = useUsers();

  const [userId, setUserId] = useState('');
  const [password, setPassword] = useState('');


  const handleSubmit = (e) => {
    e.preventDefault();

    if (!userId.trim() || !password.trim()) {
      alert('아이디와 비밀번호를 입력하세요.');
      return;
    }

    const user = login(userId, password);

    if (!user) {
      alert('아이디 또는 비밀번호가 일치하지 않습니다.');
      return;
    }

    alert(`${user.name}님, 환영합니다!`);
    navigate(ROUTES.HOME); 
  };

  return (
    <InputPage>
      <InputContainer>
        <LeftAlign>
          <TextLinks to={ROUTES.HOME} style={{ cursor: 'pointer' }}>
            ← 돌아가기
          </TextLinks>
        </LeftAlign>

        <InputMainText>우동마켓</InputMainText>
        로그인하고 거래를 시작하세요.
        <br />
        <br />

        <form onSubmit={handleSubmit}>
          <InputBox>
            아이디
            <InputBar
              type="text"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
              placeholder="아이디를 입력하세요"
            />
          </InputBox>

          <InputBox>
            비밀번호
            <InputBar
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="비밀번호를 입력하세요"
            />
          </InputBox>

          <InputButton type="submit">로그인</InputButton>
        </form>

        <HorizontalRule />

        <TextLinks to={ROUTES.SIGNUP} style={{ cursor: 'pointer' }}>
          계정이 없으신가요? 회원가입
        </TextLinks>
      </InputContainer>
    </InputPage>
  );
};

export default LoginPage;
