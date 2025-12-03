import React from 'react'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import {ROUTES} from '../routes/routePaths'
import Layout from '../Layout/Layout'
import SingupPage from '../pages/SingupPage'


const AppRoutes = () => {
  return (
    <BrowserRouter>
        <Routes>
            <Route path={ROUTES.HOME} element={<Layout/>} >
                <Route index element={<SingupPage/>}/>
            </Route>
        </Routes>
    </BrowserRouter>
  )
}

export default AppRoutes