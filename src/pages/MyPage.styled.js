import styled from 'styled-components';

export const PageContainer = styled.div`
  max-width: 560px;
  margin: 0 auto;
  padding: 32px 16px 40px;
`;

export const Card = styled.div`
  background-color: #ffffff;
  border-radius: 16px;
  padding: 20px 18px 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
`;

export const Title = styled.h1`
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 16px;
`;

export const Field = styled.div`
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 14px;
`;

export const Label = styled.label`
  font-size: 13px;
  font-weight: 600;
  color: #495057;
`;

export const Input = styled.input`
  padding: 9px 11px;
  border-radius: 10px;
  border: 1px solid #e2e2e2;
  font-size: 14px;

  &:focus {
    outline: none;
    border-color: #ff9f43;
    box-shadow: 0 0 0 2px rgba(255,159,67,0.25);
  }
`;

export const ReadOnlyValue = styled.div`
  padding: 9px 11px;
  border-radius: 10px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  font-size: 14px;
  color: #495057;
`;

export const ButtonRow = styled.div`
  display: flex;
  justify-content: space-between;
  gap: 8px;
  margin-top: 12px;
`;

export const BaseButton = styled.button`
  flex: 1;
  border: none;
  border-radius: 999px;
  padding: 9px 14px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
`;

export const PrimaryButton = styled(BaseButton)`
  background-color: #ff9f43;
  color: #ffffff;

  &:hover {
    background-color: #ff8c1a;
  }
`;

export const DangerButton = styled(BaseButton)`
  background-color: #ffe3e3;
  color: #fa5252;

  &:hover {
    background-color: #ffc9c9;
  }
`;

export const HelpText = styled.p`
  font-size: 12px;
  color: #868e96;
  margin: 4px 0 8px;
`;

export const AddressRow = styled.div`
  display: flex;
  gap: 8px;
  align-items: center;

  & > input {
    flex: 1;
  }
`;

export const AddressSearchButton = styled.button`
  border: none;
  border-radius: 999px;
  padding: 8px 12px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  background-color: #f1f3f5;
  color: #495057;

  &:hover {
    background-color: #e9ecef;
  }
`;
