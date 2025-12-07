// src/pages/MyPage.jsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUsers } from '../context/UserContext';
import {
  PageContainer,
  Card,
  Title,
  Field,
  Label,
  Input,
  ButtonRow,
  PrimaryButton,
  DangerButton,
  HelpText,
  ReadOnlyValue,
  AddressRow,
  AddressSearchButton,
} from './MyPage.styled';
import { ROUTES } from '../routes/routePaths';

// 🔹 Daum 우편번호(카카오 주소) 스크립트 로더
let postcodeLoadPromise = null;

const loadDaumPostcode = () => {
  if (typeof window === 'undefined') return Promise.reject('no-window');

  if (window.daum && window.daum.Postcode) {
    return Promise.resolve();
  }

  if (postcodeLoadPromise) return postcodeLoadPromise;

  postcodeLoadPromise = new Promise((resolve, reject) => {
    const script = document.createElement('script');
    script.src =
      '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
    script.async = true;
    script.onload = () => resolve();
    script.onerror = (err) => reject(err);
    document.head.appendChild(script);
  });

  return postcodeLoadPromise;
};

const MyPage = () => {
  const navigate = useNavigate();
  const {
    currentUser,
    updateUser,
    changeCredentials,
    deleteUser,
    logout,
  } = useUsers();

  const [name, setName] = useState('');
  const [address, setAddress] = useState('');
  const [infoMessage, setInfoMessage] = useState('');

  // 🔐 로그인 정보 변경용 상태
  const [currentPassword, setCurrentPassword] = useState('');
  const [newUserId, setNewUserId] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [newPasswordConfirm, setNewPasswordConfirm] = useState('');
  const [credMessage, setCredMessage] = useState('');

  // 로그인한 유저 정보로 초기값 세팅
  useEffect(() => {
    if (!currentUser) return;
    setName(currentUser.name || '');
    setAddress(currentUser.address || currentUser.region || '');
    setNewUserId('');
  }, [currentUser]);

  if (!currentUser) {
    return (
      <PageContainer>
        <Card>
          <Title>마이페이지</Title>
          <HelpText>로그인 후 마이페이지를 이용할 수 있습니다.</HelpText>
          <ButtonRow>
            <PrimaryButton type="button" onClick={() => navigate(ROUTES.LOGIN)}>
              로그인하러 가기
            </PrimaryButton>
          </ButtonRow>
        </Card>
      </PageContainer>
    );
  }

  // 🔹 기본 정보(이름/주소) 저장
  const handleSaveInfo = (e) => {
    e.preventDefault();
    setInfoMessage('');

    if (!name.trim() || !address.trim()) {
      setInfoMessage('이름과 주소를 모두 입력해 주세요.');
      return;
    }

    updateUser(currentUser.userId, {
      name: name.trim(),
      address: address.trim(),
    });

    setInfoMessage('회원 정보가 저장되었습니다.');
  };

  // 🔹 카카오(다음) 주소 검색 팝업
  const handleSearchAddress = () => {
    loadDaumPostcode()
      .then(() => {
        new window.daum.Postcode({
          oncomplete: (data) => {
            // 도로명 주소 또는 지번 주소
            const fullAddress = data.roadAddress || data.jibunAddress;
            setAddress(fullAddress);
          },
        }).open();
      })
      .catch((err) => {
        console.error('Daum Postcode 로딩 실패:', err);
        alert('주소 검색 서비스를 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.');
      });
  };

  // 🔹 아이디 / 비밀번호 변경
  const handleChangeCredentials = (e) => {
    e.preventDefault();
    setCredMessage('');

    if (!currentPassword) {
      setCredMessage('현재 비밀번호를 입력해 주세요.');
      return;
    }

    if (newPassword || newPasswordConfirm) {
      if (newPassword !== newPasswordConfirm) {
        setCredMessage('새 비밀번호와 확인이 일치하지 않습니다.');
        return;
      }
    }

    const result = changeCredentials({
      currentUserId: currentUser.userId,
      currentPassword,
      newUserId, // 비워두면 아이디 변경 없음
      newPassword, // 비워두면 비밀번호 변경 없음
    });

    if (!result.success) {
      setCredMessage(result.message || '변경에 실패했습니다.');
      return;
    }

    setCredMessage('로그인 정보가 변경되었습니다.');
    setCurrentPassword('');
    setNewPassword('');
    setNewPasswordConfirm('');
    setNewUserId('');
  };

  const handleDelete = () => {
    if (
      !window.confirm(
        '정말 탈퇴하시겠습니까? 탈퇴 후에는 데이터를 복구할 수 없습니다.'
      )
    ) {
      return;
    }

    deleteUser(currentUser.userId);
    logout();
    alert('탈퇴가 완료되었습니다.');
    navigate(ROUTES.HOME || '/');
  };

  return (
    <PageContainer>
      {/* 1) 기본 정보 카드 */}
      <Card as="form" onSubmit={handleSaveInfo}>
        <Title>내 정보</Title>

        {infoMessage && <HelpText>{infoMessage}</HelpText>}

        <Field>
          <Label>아이디</Label>
          <ReadOnlyValue>{currentUser.userId}</ReadOnlyValue>
        </Field>

        <Field>
          <Label>이름</Label>
          <Input
            type="text"
            placeholder="이름을 입력해 주세요."
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </Field>

        <Field>
          <Label>주소 (거래 지역)</Label>
          <AddressRow>
            <Input
              type="text"
              placeholder="예) 서울 강남구 역삼동"
              value={address}
              readOnly
            />
            <AddressSearchButton type="button" onClick={handleSearchAddress}>
              주소 검색
            </AddressSearchButton>
          </AddressRow>
          <HelpText>게시글 작성 시 이 주소가 자동으로 사용됩니다.</HelpText>
        </Field>

        <ButtonRow>
          <DangerButton type="button" onClick={handleDelete}>
            회원 탈퇴
          </DangerButton>
          <PrimaryButton type="submit">정보 저장</PrimaryButton>
        </ButtonRow>
      </Card>

      {/* 2) 로그인 정보 변경 카드 */}
      <Card
        as="form"
        onSubmit={handleChangeCredentials}
        style={{ marginTop: '20px' }}
      >
        <Title>로그인 정보 변경</Title>

        {credMessage && <HelpText>{credMessage}</HelpText>}

        <Field>
          <Label>현재 아이디</Label>
          <ReadOnlyValue>{currentUser.userId}</ReadOnlyValue>
        </Field>

        <Field>
          <Label>새 아이디 (변경 없으면 비워두세요)</Label>
          <Input
            type="text"
            placeholder="새 아이디를 입력하거나 비워두세요."
            value={newUserId}
            onChange={(e) => setNewUserId(e.target.value)}
          />
        </Field>

        <Field>
          <Label>현재 비밀번호 *</Label>
          <Input
            type="password"
            placeholder="현재 비밀번호"
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
          />
        </Field>

        <Field>
          <Label>새 비밀번호 (변경 없으면 비워두세요)</Label>
          <Input
            type="password"
            placeholder="새 비밀번호"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
          />
        </Field>

        <Field>
          <Label>새 비밀번호 확인</Label>
          <Input
            type="password"
            placeholder="새 비밀번호 확인"
            value={newPasswordConfirm}
            onChange={(e) => setNewPasswordConfirm(e.target.value)}
          />
        </Field>

        <ButtonRow>
          <PrimaryButton type="submit">로그인 정보 변경</PrimaryButton>
        </ButtonRow>
      </Card>
    </PageContainer>
  );
};

export default MyPage;
