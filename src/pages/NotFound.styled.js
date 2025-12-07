// src/pages/NotFound.styled.js
import styled from 'styled-components';

export const Wrapper = styled.div`
  width: 100%;
  height: calc(100vh - 60px); 
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 16px;
`;

export const Title = styled.h1`
  font-size: 84px;
  font-weight: 700;
  color: #ff9f43;
  margin-bottom: 12px;
`;

export const Message = styled.p`
  font-size: 18px;
  color: #495057;
  margin-bottom: 24px;
`;

export const HomeButton = styled.button`
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 600;
  background-color: #ff9f43;
  color: white;
  border: none;
  border-radius: 999px;
  cursor: pointer;

  &:hover {
    background-color: #ff8c1a;
  }
`;
