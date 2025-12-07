import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { usePosts } from '../context/PostContext';  
import { useUsers } from '../context/UserContext';
import {
  PageContainer,
  FormContainer,
  FormTitle,
  Field,
  Label,
  Input,
  Select,
  TextArea,
  ButtonRow,
  SubmitButton,
  CancelButton,
  HelpText,
  ImageUploadWrapper,
  HiddenFileInput,
  UploadButton,
  ImagePreview,
} from './PostCreatePage.styled';

const CATEGORIES = ['전자기기', '가구', '생활용품', '의류', '도서', '스포츠', '기타'];

const PostCreatePage = () => {
  const navigate = useNavigate();
  const { addPost } = usePosts();
  const { currentUser } = useUsers();

  const [title, setTitle] = useState('');
  const [price, setPrice] = useState('');
  const [category, setCategory] = useState(CATEGORIES[0]);
  const [region, setRegion] = useState('');
  const [imageDataUrl, setImageDataUrl] = useState(''); 
  const [description, setDescription] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    if (!currentUser) return;


    const userAddress = currentUser.address || currentUser.region || '';
    if (userAddress && !region) {
      setRegion(userAddress);
    }
  }, [currentUser, region]);

  const handleImageChange = (e) => {
    const file = e.target.files && e.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (event) => {
      setImageDataUrl(event.target.result); 
    };
    reader.readAsDataURL(file);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!currentUser) {
      alert('로그인이 필요합니다.');
      navigate('/login');
      return;
    }

    if (!title.trim() || !price || !region.trim() || !description.trim()) {
      setErrorMsg('필수 항목을 모두 입력해 주세요.');
      return;
    }

    const priceNumber = Number(price);
    if (Number.isNaN(priceNumber) || priceNumber <= 0) {
      setErrorMsg('가격은 0보다 큰 숫자로 입력해 주세요.');
      return;
    }

    const newPost = {
      title: title.trim(),
      price: priceNumber,
      image: imageDataUrl,       
      category,
      status: '판매중',
      region: region.trim(),   
      sellerId: currentUser.userId,
      description: description.trim(),

    };

    addPost(newPost);
    navigate('/');
  };

  const handleCancel = () => {
    navigate(-1);
  };

  return (
    <PageContainer>
      <FormContainer onSubmit={handleSubmit}>
        <FormTitle>게시물 등록</FormTitle>

        {errorMsg && <HelpText>{errorMsg}</HelpText>}

        <Field>
          <Label>제목 *</Label>
          <Input
            type="text"
            placeholder="예) 아이폰 13 미개봉 판매합니다"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
        </Field>

        <Field>
          <Label>가격(원) *</Label>
          <Input
            type="number"
            min="0"
            inputMode="numeric"
            placeholder="예) 500000"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
          />
        </Field>

        <Field>
          <Label>카테고리 *</Label>
          <Select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
          >
            {CATEGORIES.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </Select>
        </Field>

        <Field>
          <Label>거래 지역 *</Label>
          <Input
            type="text"
            placeholder={
              currentUser
                ? '내 프로필의 주소가 자동으로 입력됩니다.'
                : '로그인 후 주소가 자동으로 입력됩니다.'
            }
            value={region}
            readOnly   
          />
          <HelpText>주소 변경은 마이페이지에서 수정해 주세요.</HelpText>
        </Field>

        <Field>
          <Label>이미지 업로드</Label>
          <ImageUploadWrapper>
            <HiddenFileInput
              id="post-image-input"
              type="file"
              accept="image/*"
              onChange={handleImageChange}
            />
            <UploadButton
              type="button"
              onClick={() => {
                const input = document.getElementById('post-image-input');
                if (input) input.click();
              }}
            >
              이미지 선택
            </UploadButton>
            {imageDataUrl && (
              <ImagePreview src={imageDataUrl} alt="미리보기" />
            )}
          </ImageUploadWrapper>
        </Field>

        <Field>
          <Label>상세 설명 *</Label>
          <TextArea
            rows={6}
            placeholder="상품 상태, 사용 기간, 구성품, 하자 여부 등을 자세히 써 주세요."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </Field>

        <ButtonRow>
          <CancelButton type="button" onClick={handleCancel}>
            취소
          </CancelButton>
          <SubmitButton type="submit">
            등록하기
          </SubmitButton>
        </ButtonRow>
      </FormContainer>
    </PageContainer>
  );
};

export default PostCreatePage;
