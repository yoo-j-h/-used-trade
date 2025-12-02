import React from 'react'
import { useLocation } from 'react-router-dom'
import { HeaderContainer, Logo, Nav, NavLinks } from './Layout.styled';
import { ROUTES } from '../routes/routePaths';

const Header = () => {
    const location = useLocation(); 

    const isActive =(path)=>{
        return location.pathname === path ? 'active' : '';
    }
  return (
    <HeaderContainer>
    <Nav>
        <Logo to={ROUTES.HOME}>Todo App</Logo>
        <NavLinks>
        </NavLinks>
      </Nav>
    </HeaderContainer>
  )
}

export default Header