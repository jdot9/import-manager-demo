import './App.css'
import './components/ui/Navbar.css'
import ConnectionPage from './pages/ConnectionPage'
import { BrowserRouter, Routes, Route, Navigate } from "react-router";
import './index.css'
import MainLayout from './components/ui/MainLayout'
import ImportPage from './pages/ImportPage'
import LoginPage from './pages/LoginPage'
import ProtectedRoute from './components/ProtectedRoute'
import RegistrationPage from './pages/RegistrationPage'
import ForgotPasswordPage from './pages/ForgotPasswordPage'

function App() {
 
  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/registration" element={<RegistrationPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />
        
        {/* Protected routes - require authentication */}
        <Route
          path="/*"
          element={
            <ProtectedRoute>
              <MainLayout>
                <Routes>
                  <Route path="/" element={<Navigate to="/connections" replace />} />
                  <Route path="/connections" element={<ConnectionPage />} />
                  <Route path="/imports" element={<ImportPage />} />
                </Routes>
              </MainLayout>
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  )
}

export default App
