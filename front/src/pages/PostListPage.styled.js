import styled from "styled-components";

export const PageContainer = styled.div`
  max-width: 960px;
  margin: 0 auto;
  padding: 24px 16px 40px;
`;

export const HeaderRow = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
`;

export const TitleText = styled.h1`
  font-size: 22px;
  font-weight: 700;
  margin: 0;
`;

export const NewPostButton = styled.button`
  border: none;
  border-radius: 999px;
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  background-color: #ff9f43;
  color: #ffffff;

  &:hover {
    background-color: #ff8c1a;
  }
`;

export const CategoryFilterBar = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
`;

export const CategoryButton = styled.button`
  border: none;
  border-radius: 999px;
  padding: 6px 12px;
  font-size: 13px;
  cursor: pointer;
  background-color: ${({ $active }) => ($active ? "#ff9f43" : "#f1f3f5")};
  color: ${({ $active }) => ($active ? "#ffffff" : "#495057")};
  font-weight: ${({ $active }) => ($active ? 700 : 500)};

  &:hover {
    background-color: ${({ $active }) => ($active ? "#ff8c1a" : "#e9ecef")};
  }
`;

export const CardsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 12px;
`;

export const EmptyMessage = styled.p`
  font-size: 14px;
  color: #868e96;
  text-align: center;
  margin-top: 40px;
`;
