import React from 'react'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import {ROUTES} from '../routes/routePaths'
import Layout from '../Layout/Layout'
import SingupPage from '../pages/SignupPage'
import LoginPage from '../pages/LoginPage'
import PostCreatePage from '../pages/PostCreatePage'
import PostListPage from '../pages/PostListPage'
import PostDetailPage from '../pages/PostDetailPage'
import MyPage from '../pages/MyPage'
import NotFound from '../pages/NotFound'


const AppRoutes = () => {
  return (
    <BrowserRouter>
        <Routes>
            <Route path={ROUTES.HOME} element={<Layout/>} >
                <Route index element={<PostListPage/>}/>
                <Route path={ROUTES.POST} element={<PostCreatePage/>}/>
                <Route path={ROUTES.SIGNUP} element={<SingupPage/>}/>
                <Route path={ROUTES.LOGIN} element = {<LoginPage/>}/>
                <Route path="/posts/:postId" element={<PostDetailPage />} />
                <Route path={ROUTES.MYPAGE} element={<MyPage/>}/>
                <Route path="*" element={<NotFound/>}/>
            </Route>
        </Routes>
    </BrowserRouter>
  )
}

export default AppRoutes