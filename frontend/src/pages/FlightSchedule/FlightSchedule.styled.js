import styled from "styled-components";

/**
 * ✅ 이 페이지는 Layout(사이드바/탑바) 바깥을 책임지지 않음.
 * ✅ default layout 내부에서 렌더링되는 "컨텐츠"만 스타일링.
 */

export const PageContainer = styled.div`
  width: 100%;
  max-width: 1600px;
  margin: 0 auto;
  padding: 32px 48px;

  @media (max-width: 1440px) {
    padding: 24px 32px;
  }

  @media (max-width: 1024px) {
    padding: 20px 24px;
  }
`;

// ==================== Header ====================
export const PageHeader = styled.header`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;

  @media (max-width: 1024px) {
    flex-direction: column;
    gap: 12px;
  }
`;

export const HeaderLeft = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

export const BreadcrumbText = styled.p`
  font-size: 14px;
  color: ${({ theme }) => theme.text.secondary};
  margin: 0;
`;

export const PageTitle = styled.h1`
  font-size: 28px;
  font-weight: 700;
  color: ${({ theme }) => theme.text.primary};
  margin: 0;
`;

export const PageSubtitle = styled.p`
  font-size: 15px;
  color: ${({ theme }) => theme.text.secondary};
  margin: 0;
`;

// ==================== Filter Section ====================
export const FilterSection = styled.section`
  display: flex;
  gap: 16px;
  padding: 20px;
  background-color: ${({ theme }) => theme.background.secondary};
  border-radius: 12px;
  box-shadow: ${({ theme }) => theme.shadow};
  margin-bottom: 20px;
  flex-wrap: wrap;

  @media (max-width: 1024px) {
    flex-direction: column;
  }
`;

export const FilterGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 180px;
`;

export const FilterLabel = styled.label`
  font-size: 14px;
  font-weight: 600;
  color: ${({ theme }) => theme.text.primary};
`;

export const FilterButtonGroup = styled.div`
  display: flex;
  gap: 8px;
`;

export const FilterButton = styled.button`
  padding: 10px 18px;
  font-size: 14px;
  font-weight: 600;
  border: 1px solid ${(props) => (props.$active ? props.theme.colors.primary : props.theme.border)};
  background-color: ${(props) => (props.$active ? props.theme.background.secondary : props.theme.background.paper)};
  color: ${(props) => (props.$active ? props.theme.colors.primary : props.theme.text.secondary)};
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background-color: ${(props) => (props.$active ? props.theme.background.hover : props.theme.background.secondary)};
    filter: brightness(0.95);
  }
`;

export const DateInput = styled.input`
  padding: 10px 14px;
  font-size: 14px;
  border: 1px solid ${({ theme }) => theme.border};
  border-radius: 10px;
  background-color: ${({ theme }) => theme.background.input};
  color: ${({ theme }) => theme.text.primary};

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary};
    box-shadow: 0 0 0 3px ${props => props.theme.colors.primary}20;
  }
`;

export const CitySelect = styled.select`
  padding: 10px 14px;
  font-size: 14px;
  border: 1px solid ${({ theme }) => theme.border};
  border-radius: 10px;
  background-color: ${({ theme }) => theme.background.input};
  color: ${({ theme }) => theme.text.primary};

  &:focus {
    outline: none;
    border-color: ${props => props.theme.colors.primary};
    box-shadow: 0 0 0 3px ${props => props.theme.colors.primary}20;
  }
`;

export const SearchButton = styled.button`
  align-self: flex-end;
  padding: 10px 28px;
  font-size: 15px;
  font-weight: 700;
  background: ${props => `linear-gradient(135deg, ${props.theme.colors.primary} 0%, ${props.theme.colors.secondary} 100%)`};
  color: ${({ theme }) => theme.text.inverse || 'white'};
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 16px ${props => props.theme.colors.primary}38;
  }

  &:active {
    transform: translateY(0);
  }
`;

// ==================== Flight List ====================
export const FlightListContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 16px;
`;

export const FlightCard = styled.div`
  background-color: ${({ theme }) => theme.background.secondary};
  border-radius: 14px;
  padding: 22px;
  box-shadow: ${({ theme }) => theme.shadow};
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: pointer;

  &:hover {
    box-shadow: ${({ theme }) => theme.shadowHover};
    transform: translateY(-2px);
  }
`;

export const MessageContainer = styled.div`
  padding: 40px;
  text-align: center;
  color: ${({ theme }) => theme.text.secondary};
`;

export const CardHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;

  @media (max-width: 1024px) {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
`;

export const FlightBadge = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
`;

export const AirlineIcon = styled.div`
  width: 46px;
  height: 46px;
  background: ${props => `linear-gradient(135deg, ${props.theme.colors.primary} 0%, ${props.theme.colors.secondary} 100%)`};
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: ${({ theme }) => theme.text.inverse || 'white'};
`;

export const FlightNumber = styled.h3`
  font-size: 18px;
  font-weight: 800;
  color: ${({ theme }) => theme.text.primary};
  margin: 0;
`;

export const FlightDate = styled.p`
  font-size: 13px;
  color: ${({ theme }) => theme.text.secondary};
  margin: 4px 0 0 0;
`;

export const StatusBadgeGroup = styled.div`
  display: flex;
  gap: 8px;
`;

export const StatusBadge = styled.span`
  padding: 7px 14px;
  font-size: 13px;
  font-weight: 700;
  border-radius: 999px;

  background-color: ${(props) => (props.$status === "normal" ? `${props.theme.colors.primary}15` : `${props.theme.status.success}15`)};
  color: ${(props) => (props.$status === "normal" ? props.theme.colors.primary : props.theme.status.success)};
`;

// ==================== Flight Route ====================

export const FlightRoute = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 32px;

  @media (max-width: 1024px) {
    flex-direction: column;
    gap: 14px;
  }
`;

export const RoutePoint = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
`;

export const RouteTime = styled.p`
  font-size: 24px;
  font-weight: 800;
  color: ${({ theme }) => theme.text.primary};
  margin: 0;
`;

export const RouteCode = styled.p`
  font-size: 16px;
  font-weight: 800;
  color: ${({ theme }) => theme.colors.primary};
  margin: 0;
`;

export const RouteAirport = styled.p`
  font-size: 13px;
  color: ${({ theme }) => theme.text.secondary};
  margin: 0;
`;

export const RouteIndicator = styled.div`
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  position: relative;
`;

export const AirplaneIcon = styled.div`
  width: 34px;
  height: 34px;
  border-radius: 999px;
  background: ${({ theme }) => `${theme.colors.primary}15`};
  color: ${({ theme }) => theme.colors.primary};
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
`;

export const RouteLine = styled.div`
  width: 100%;
  height: 2px;
  background: ${props => `linear-gradient(90deg, ${props.theme.colors.primary} 0%, ${props.theme.colors.secondary} 100%)`};
  border-radius: 2px;
`;

export const RouteDuration = styled.p`
  font-size: 13px;
  color: ${({ theme }) => theme.text.secondary};
  margin: 0;
`;
