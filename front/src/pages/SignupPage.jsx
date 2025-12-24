import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  DuplicatecheckButton,
  HorizontalRule,
  InputBar,
  InputBox,
  InputButton,
  InputContainer,
  InputJustify,
  InputMainText,
  InputPage,
  LeftAlign,
  TextLinks,
} from './Input.styled';
import { useDaumPostcodePopup } from 'react-daum-postcode';
import { useUsers } from '../context/UsersContext';
import { ROUTES } from '../routes/routePaths';

const SingupPage = () => {
  const navigate = useNavigate();
  const { users, addUser } = useUsers();

  const postcodeScriptUrl =
    'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
  const open = useDaumPostcodePopup(postcodeScriptUrl);

  const [userId, setUserId] = useState('');
  const [userName, setUserName] = useState(''); // ✅ name -> userName
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [passwordConfirm, setPasswordConfirm] = useState('');
  const [address, setAddress] = useState('');
  const [idChecked, setIdChecked] = useState(false);

  const formatPhoneNumber = (value) => {
    let digits = value.replace(/\D/g, '');
    digits = digits.substring(0, 11);

    if (digits.length < 4) return digits;
    if (digits.length < 8)
      return digits.replace(/(\d{3})(\d{1,4})/, '$1-$2');

    return digits.replace(/(\d{3})(\d{4})(\d{1,4})/, '$1-$2-$3');
  };

  const handlePhoneChange = (e) => {
    setPhone(formatPhoneNumber(e.target.value));
  };

  const handleIdCheck = () => {
    if (!userId.trim()) {
      alert('아이디를 입력하세요.');
      return;
    }

    // users는 camelCase
    const exists = users.some((user) => user.userId === userId);

    if (exists) {
      alert('이미 사용 중인 아이디입니다.');
      setIdChecked(false);
    } else {
      alert('사용 가능한 아이디입니다!');
      setIdChecked(true);
    }
  };

  const handleAddressSearch = () => {
    open({
      onComplete: (data) => {
        let fullAddress = data.address;
        let extraAddress = '';
        const localAddress = `${data.sido} ${data.sigungu}`;

        if (data.addressType === 'R') {
          if (data.bname !== '') extraAddress += data.bname;
          if (data.buildingName !== '')
            extraAddress += extraAddress
              ? `, ${data.buildingName}`
              : data.buildingName;

          fullAddress = fullAddress.replace(localAddress, '');
          fullAddress += extraAddress ? ` (${extraAddress})` : '';
        }

        setAddress(fullAddress.trim());
      },
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (
      !userId.trim() ||
      !userName.trim() ||
      !phone.trim() ||
      !password.trim() ||
      !passwordConfirm.trim() ||
      !address.trim()
    ) {
      alert('모든 필드를 입력해주세요.');
      return;
    }

    if (password !== passwordConfirm) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }

    const exists = users.some((u) => u.userId === userId);
    if (exists) {
      alert('이미 사용 중인 아이디입니다.');
      return;
    }

    if (!idChecked) {
      alert('아이디 중복체크를 먼저 해주세요.');
      return;
    }

    // ✅ camelCase로만 작성 (UsersContext가 snake로 변환해서 전송)
    const signupData = {
      userId,
      userName,
      password,
      email: null,
      phone,
      address,
    };

    try {
      await addUser(signupData);
      alert('회원가입이 완료되었습니다.');
      navigate(ROUTES.LOGIN);
    } catch (err) {
      alert('회원가입에 실패했습니다.');
      console.error("signup failed", err);
    }
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
            <InputJustify>
              <InputBar
                type="text"
                value={userId}
                onChange={(e) => {
                  setUserId(e.target.value);
                  setIdChecked(false);
                }}
                placeholder="아이디를 입력하세요"
              />
              <DuplicatecheckButton type="button" onClick={handleIdCheck}>
                중복체크
              </DuplicatecheckButton>
            </InputJustify>
          </InputBox>

          <InputBox>
            이름
            <InputBar
              type="text"
              value={userName}
              onChange={(e) => setUserName(e.target.value)}
              placeholder="이름을 입력하세요"
            />
          </InputBox>

          <InputBox>
            전화번호
            <InputBar
              type="tel"
              value={phone}
              onChange={handlePhoneChange}
              placeholder="'-' 없이 숫자만 입력"
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

          <InputBox>
            비밀번호 확인
            <InputBar
              type="password"
              value={passwordConfirm}
              onChange={(e) => setPasswordConfirm(e.target.value)}
              placeholder="비밀번호를 다시 입력하세요"
            />
          </InputBox>

          <InputBox>
            주소
            <InputJustify>
              <InputBar
                type="text"
                value={address}
                readOnly
                placeholder="주소를 검색하세요"
              />
              <DuplicatecheckButton type="button" onClick={handleAddressSearch}>
                주소검색
              </DuplicatecheckButton>
            </InputJustify>
          </InputBox>

          <InputButton type="submit">회원가입</InputButton>
        </form>

        <HorizontalRule />

        <TextLinks to={ROUTES.LOGIN} style={{ cursor: 'pointer' }}>
          이미 계정이 있으신가요? 로그인
        </TextLinks>
      </InputContainer>
    </InputPage>
  );
};

export default SingupPage;
