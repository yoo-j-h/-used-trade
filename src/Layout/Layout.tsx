import React from 'react'
import Header from './Header'
import { LayoutContainer, MainContent } from './Layout.styled'
import { Outlet } from 'react-router-dom'

const Layout = () => {
  return (
    <LayoutContainer>
        <Header/>
        <MainContent>
            <Outlet />
        </MainContent>
    </LayoutContainer>
    
  )
}

export default Layout