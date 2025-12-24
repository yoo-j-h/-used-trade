import React, { useState, useRef, useEffect } from 'react';
import { useLocation, Link, useNavigate } from 'react-router-dom';
import {
  HeaderContainer,
  Logo,
  Nav,
  NavLinks,
  NavLink,
  ProfileWrapper,
  ProfileButton,
  DropdownMenu,
  DropdownItem
} from './Layout.styled';

import { ROUTES } from '../routes/routePaths';
import { useUsers } from '../context/UsersContext';

const Header = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { currentUser, logout } = useUsers();

  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);

  const isActive = (path) => (location.pathname === path ? "active" : "");

  // 🔹 외부 클릭 시 드롭다운 닫기
  useEffect(() => {
    const close = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", close);
    return () => document.removeEventListener("mousedown", close);
  }, []);

  return (
    <HeaderContainer>
      <Nav>
        <Logo to={ROUTES.HOME}>우동마켓</Logo>

        <NavLinks>
          {!currentUser ? (
            <>
              <NavLink to={ROUTES.LOGIN} className={isActive(ROUTES.LOGIN)}>
                로그인
              </NavLink>
              <NavLink to={ROUTES.SIGNUP} className={isActive(ROUTES.SIGNUP)}>
                회원가입
              </NavLink>
            </>
          ) : (
            <ProfileWrapper ref={dropdownRef}>
              <ProfileButton onClick={() => setOpen((v) => !v)}>
                {currentUser.name} <span style={{ fontSize: "10px" }}>{open ? "▲" : "▼"}</span>
              </ProfileButton>

              {open && (
                <DropdownMenu>
                  <DropdownItem onClick={() => navigate(ROUTES.MYPAGE)}>
                    마이페이지
                  </DropdownItem>
                  <DropdownItem
                    className="danger"
                    onClick={() => {
                      logout();
                      setOpen(false);
                    }}
                  >
                    로그아웃
                  </DropdownItem>
                </DropdownMenu>
              )}
            </ProfileWrapper>
          )}
        </NavLinks>
      </Nav>
    </HeaderContainer>
  );
};

export default Header;
