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
        color: #db791dff;
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

export const MainContent = styled.main`
    max-width: 1200px;
    margin: 0 auto;
    padding: 24px;
    height : calc(100vh - 64px);
`
