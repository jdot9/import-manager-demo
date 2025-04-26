import './App.css'
import './components/ui/Navbar.css'
import ConnectionPage from './pages/ConnectionPage'
import { BrowserRouter, Routes, Route, Navigate } from "react-router";
import './index.css'
import MainLayout from './components/ui/MainLayout'
import ImportPage from './pages/ImportPage'
import LoginPage from './pages/LoginPage'
import ProfilePage from './pages/ProfilePage'
import ProtectedRoute from './components/ProtectedRoute'
import RegistrationPage from './pages/RegistrationPage';

function App() {
 
  return (
    <BrowserRouter>
      <Routes>
        {/* Public route - Login */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/registration" element={<RegistrationPage />} />
        
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
                  <Route path="/profile" element={<ProfilePage />} />
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
