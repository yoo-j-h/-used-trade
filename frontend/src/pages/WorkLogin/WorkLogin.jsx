import React, { useState } from 'react';
import { S } from './WorkLogin.styled';

const AttendanceLogin = () => {
  const [formData, setFormData] = useState({
    userId: '',
    password: ''
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // TODO: Zustand store action - validateAttendanceLogin(formData)
    console.log('Attendance login attempt:', formData);

    // [QA Mock] 백엔드 연동 전 라우팅 테스트를 위한 임시 네비게이션
    // 실제 로직: 출근 처리 후 결과 모달 -> 닫기 후 랜딩으로 이동
    alert(`[테스트] ${formData.userId}님 출근(또는 퇴근) 처리가 완료되었습니다.`);
    window.location.href = '/';
  };

  return (
    <>
      <S.Container>
        <S.LoginWrapper>
          <S.LoginCard>
            <S.CardHeader>
              <S.Title>출/퇴근 로그인</S.Title>
              <S.Subtitle>아이디과 비밀번호를 입력하여 로그인해주세요</S.Subtitle>
            </S.CardHeader>

            <S.LoginForm onSubmit={handleSubmit}>
              <S.InputGroup>
                <S.InputLabel>
                  <S.UserIdIcon />
                  아이디
                </S.InputLabel>
                <S.Input
                  type="text"
                  name="userId"
                  placeholder="아이디를 입력하세요"
                  value={formData.userId}
                  onChange={handleInputChange}
                  required
                />
              </S.InputGroup>

              <S.InputGroup>
                <S.InputLabel>
                  <S.PasswordIcon />
                  비밀번호
                </S.InputLabel>
                <S.Input
                  type="password"
                  name="password"
                  placeholder="비밀번호를 입력하세요"
                  value={formData.password}
                  onChange={handleInputChange}
                  required
                />
              </S.InputGroup>

              <S.SubmitButton type="submit">
                로그인
                <S.ArrowIcon />
              </S.SubmitButton>
            </S.LoginForm>
          </S.LoginCard>
        </S.LoginWrapper>
      </S.Container>
    </>
  );
};

export default AttendanceLogin;