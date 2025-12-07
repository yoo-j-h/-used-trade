// src/components/PostCard.styled.js
import styled from "styled-components";

export const CardContainer = styled.div`
  background-color: #ffffff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: transform 0.08s ease, box-shadow 0.12s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }
`;

export const ThumbnailWrapper = styled.div`
  width: 100%;
  padding-top: 70%; /* 7:10 비율 */
  position: relative;
  overflow: hidden;
`;

export const Thumbnail = styled.img`
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

export const StatusOverlay = styled.div`
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 2px;

  ${({ $status }) => {
    if ($status === "판매완료") {
      return `
        background-color: rgba(0, 0, 0, 0.7);
        color: #ffffff;
      `;
    }
    if ($status === "예약중") {
      return `
        background-color: rgba(255, 193, 7, 0.8);
        color: #212529;
      `;
    }
    return `
      background-color: transparent;
      color: inherit;
      pointer-events: none;
    `;
  }}
`;

export const InfoContainer = styled.div`
  padding: 10px 12px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
`;

export const Title = styled.h2`
  font-size: 14px;
  font-weight: 600;
  margin: 0;
  line-height: 1.3;
`;

export const Price = styled.div`
  font-size: 15px;
  font-weight: 700;
  color: #212529;
`;

export const MetaRow = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
  margin-top: 2px;
`;

export const RegionText = styled.span`
  font-size: 12px;
  color: #868e96;
`;

export const CategoryTag = styled.span`
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 999px;
  background-color: #f1f3f5;
  color: #495057;
`;

export const StatusBadge = styled.span`
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 999px;
  font-weight: 600;
  ${({ $status }) => {
    if ($status === "판매완료") {
      return `
        background-color: #e9ecef;
        color: #868e96;
      `;
    }
    if ($status === "예약중") {
      return `
        background-color: #fff3bf;
        color: #e67700;
      `;
    }
    // 기본: 판매중
    return `
      background-color: #d3f9d8;
      color: #2b8a3e;
    `;
  }}
`;
