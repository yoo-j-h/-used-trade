import { Link } from "react-router-dom"
import styled from "styled-components"

export const LayoutContainer = styled.div`
    min-height: 100vh;
    background: #f3f2f2;
`

export const HeaderContainer = styled.header`
    padding: 0 15px;
    background: #ffffff;
`

export const Nav = styled.nav`
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 64px;
`

export const Logo = styled(Link)`
    font-size: 24px;
    font-weight: 900;
    color: #fd8b20ff;
    text-decoration: none;

    &:hover{
        color: #db791d1d;
        scale: 1.02;
    }
`

export const NavLinks = styled.div`
    display: flex;
    align-items: center;
    gap: 24px;
`

export const NavLink = styled(Link)`
    color: #333;
    text-decoration: none;
    font-size: 16px;
    padding: 8px 12px;
    transition: all 0.2s;
    border-radius: 4px;
    font-weight: 600;
    
    &:hover{
        color: #fd8b20ff;
        background: #f0f0f0;
    }

    &.active{
        color: #fd8b20ff;
        background: #ffffff;
    }
`

/* ------------------------------------------
   🔥 드롭다운 메뉴 스타일 추가
------------------------------------------ */

export const ProfileWrapper = styled.div`
    position: relative;
`;

export const ProfileButton = styled.button`
    padding: 8px 14px;
    background: #ffffff;
    border: 1px solid #ddd;
    border-radius: 999px;
    font-size: 14px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 6px;

    &:hover {
        background: #f8f8f8;
    }
`;

export const DropdownMenu = styled.div`
    position: absolute;
    right: 0;
    top: 48px;
    min-width: 150px;
    background: #ffffff;
    border-radius: 8px;
    border: 1px solid #eee;
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
    overflow: hidden;
    z-index: 1000;
`;

export const DropdownItem = styled.button`
    width: 100%;
    padding: 10px 14px;
    text-align: left;
    background: white;
    border: none;
    font-size: 14px;
    cursor: pointer;

    &:hover {
        background: #f7f7f7;
    }

    &.danger {
        color: #e53935;

        &:hover {
            background: #fff5f5;
        }
    }
`;

export const MainContent = styled.main`
    max-width: 1200px;
    margin: 0 auto;
    padding: 24px;
    height : calc(100vh - 64px);
`
