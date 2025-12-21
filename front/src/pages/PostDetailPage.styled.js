import styled from "styled-components";

export const PageContainer = styled.div`
  max-width: 960px;
  margin: 0 auto;
  padding: 24px 16px 40px;
`;

export const TopLayout = styled.div`
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(0, 1fr);
  gap: 20px;
  margin-bottom: 24px;

  @media (max-width: 768px) {
    grid-template-columns: minmax(0, 1fr);
  }
`;

export const ImageBox = styled.div`
  border-radius: 16px;
  overflow: hidden;
  background-color: #f1f3f5;
`;

export const MainImage = styled.img`
  width: 100%;
  height: 100%;
  max-height: 360px;
  object-fit: cover;
`;

export const InfoBox = styled.div`
  padding: 8px 4px;
  display: flex;
  flex-direction: column;
  gap: 6px;
`;

export const Title = styled.h1`
  font-size: 20px;
  font-weight: 700;
  margin: 0;
`;

export const Price = styled.div`
  font-size: 18px;
  font-weight: 700;
  margin-top: 4px;
`;

export const MetaRow = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-top: 4px;
`;

export const RegionText = styled.span`
  font-size: 13px;
  color: #868e96;
`;

export const CategoryTag = styled.span`
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 999px;
  background-color: #f1f3f5;
  color: #495057;
`;

export const StatusBadge = styled.span`
  font-size: 12px;
  padding: 3px 10px;
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
    return `
      background-color: #d3f9d8;
      color: #2b8a3e;
    `;
  }}
`;

export const Description = styled.p`
  font-size: 14px;
  margin-top: 8px;
  color: #495057;
  white-space: pre-wrap;
`;

export const SectionTitle = styled.h2`
  font-size: 16px;
  font-weight: 700;
  margin: 20px 0 8px;
`;

export const EditForm = styled.form`
  display: flex;
  flex-direction: column;
  gap: 8px;
  background-color: #ffffff;
  border-radius: 16px;
  padding: 16px 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
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
  min-height: 80px;

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
`;

export const BaseButton = styled.button`
  border: none;
  border-radius: 999px;
  padding: 8px 16px;
  font-size: 13px;
  cursor: pointer;
  font-weight: 600;
`;

export const PrimaryButton = styled(BaseButton)`
  background-color: #ff9f43;
  color: #ffffff;

  &:hover {
    background-color: #ff8c1a;
  }
`;

export const SecondaryButton = styled(BaseButton)`
  background-color: #f1f3f5;
  color: #495057;

  &:hover {
    background-color: #e9ecef;
  }
`;

export const CommentSection = styled.div`
  margin-top: 8px;
`;

export const CommentForm = styled.form`
  margin-bottom: 12px;
`;

export const CommentTextarea = styled.textarea`
  width: 100%;
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid #e2e2e2;
  font-size: 14px;
  resize: vertical;
  min-height: 60px;

  &:focus {
    outline: none;
    border-color: #ff9f43;
    box-shadow: 0 0 0 2px rgba(255, 159, 67, 0.25);
  }

  &:disabled {
    background-color: #f8f9fa;
  }
`;

export const CommentList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 6px;
`;

export const CommentItem = styled.div`
  padding: 8px 10px;
  border-radius: 10px;
  background-color: ${({ $isReply }) => ($isReply ? "#f8f9fa" : "#ffffff")};
  margin-left: ${({ $isReply }) => ($isReply ? "16px" : "0")};
  border: 1px solid #e9ecef;
`;

export const CommentMeta = styled.div`
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: #868e96;
  margin-bottom: 2px;
`;

export const CommentAuthor = styled.span`
  font-weight: 600;
`;

export const CommentContent = styled.p`
  font-size: 13px;
  margin: 0;
  color: #495057;
  white-space: pre-wrap;
`;

export const CommentDeleteButton = styled.button`
  border: none;
  background: none;
  color: #fa5252;
  font-size: 11px;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
`;

export const ReplyForm = styled.form`
  margin: 4px 0 8px 16px;
`;

export const ReplyTextarea = styled.textarea`
  width: 100%;
  padding: 8px 10px;
  border-radius: 10px;
  border: 1px solid #e2e2e2;
  font-size: 13px;
  resize: vertical;
  min-height: 50px;

  &:focus {
    outline: none;
    border-color: #ff9f43;
    box-shadow: 0 0 0 2px rgba(255, 159, 67, 0.25);
  }
`;

export const ReplyButton = styled.button`
  border: none;
  background: none;
  color: #228be6;
  font-size: 11px;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
`;

export const StatusButtonsWrapper = styled.div`
  display: flex;
  gap: 4px;
`;

export const StatusChangeButton = styled.button`
  border: none;
  border-radius: 999px;
  padding: 3px 8px;
  font-size: 11px;
  cursor: pointer;
  background-color: ${({ $active }) => ($active ? "#ff9f43" : "#f1f3f5")};
  color: ${({ $active }) => ($active ? "#ffffff" : "#495057")};
  font-weight: 600;

  &:hover {
    background-color: ${({ $active }) => ($active ? "#ff8c1a" : "#e9ecef")};
  }
`;

export const EmptyMessage = styled.p`
  font-size: 14px;
  color: #868e96;
  text-align: center;
  margin-top: 24px;
`;
