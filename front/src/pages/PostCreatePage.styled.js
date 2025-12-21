// src/pages/PostCreatePage.styled.js
import styled from "styled-components";

export const PageContainer = styled.div`
  max-width: 640px;
  margin: 0 auto;
  padding: 32px 16px;
`;

export const FormContainer = styled.form`
  background-color: #ffffff;
  border-radius: 16px;
  padding: 24px 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
`;

export const FormTitle = styled.h1`
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 20px;
`;

export const Field = styled.div`
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
`;

export const Label = styled.label`
  font-size: 14px;
  font-weight: 600;
`;

export const Input = styled.input`
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid #e2e2e2;
  font-size: 14px;

  &:focus {
    outline: none;
    border-color: #ff9f43;
    box-shadow: 0 0 0 2px rgba(255, 159, 67, 0.25);
  }
`;

export const Select = styled.select`
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid #e2e2e2;
  font-size: 14px;

  &:focus {
    outline: none;
    border-color: #ff9f43;
    box-shadow: 0 0 0 2px rgba(255, 159, 67, 0.25);
  }
`;

export const TextArea = styled.textarea`
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid #e2e2e2;
  font-size: 14px;
  resize: vertical;
  min-height: 120px;

  &:focus {
    outline: none;
    border-color: #ff9f43;
    box-shadow: 0 0 0 2px rgba(255, 159, 67, 0.25);
  }
`;

export const ButtonRow = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
`;

export const BaseButton = styled.button`
  border: none;
  border-radius: 999px;
  padding: 10px 18px;
  font-size: 14px;
  cursor: pointer;
  font-weight: 600;
`;

export const SubmitButton = styled(BaseButton)`
  background-color: #ff9f43;
  color: #ffffff;

  &:hover {
    background-color: #ff8c1a;
  }
`;

export const CancelButton = styled(BaseButton)`
  background-color: #f1f3f5;
  color: #495057;

  &:hover {
    background-color: #e9ecef;
  }
`;

export const HelpText = styled.p`
  font-size: 12px;
  color: #868e96;
  margin: 0;
`;



export const ImageUploadWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
`;

export const HiddenFileInput = styled.input`
  display: none;
`;

export const UploadButton = styled(BaseButton)`
  background-color: #ffe8cc;
  color: #d9480f;

  &:hover {
    background-color: #ffd8a8;
  }
`;

export const ImagePreview = styled.img`
  width: 96px;
  height: 96px;
  border-radius: 12px;
  object-fit: cover;
  border: 1px solid #e2e2e2;
`;
