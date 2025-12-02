import React from 'react'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import {ROUTES} from '../routes/routePaths'
import Layout from '../Layout/Layout'


const AppRoutes = () => {
  return (
    <BrowserRouter>
        <Routes>
            <Route path={ROUTES.HOME} element={<Layout/>} >

            </Route>
        </Routes>
    </BrowserRouter>
  )
}

export default AppRoutes