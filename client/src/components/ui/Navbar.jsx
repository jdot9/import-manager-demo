import { NavLink } from "react-router";
import { useNavigate } from "react-router";
import UserService from '../../services/UserService';
import './Navbar.css'

function Navbar() {
  const navigate = useNavigate();
  
  const handleLogout = () => {
    UserService.logout();
    navigate('/login');
  };

  const currentUser = UserService.getCurrentUser();

  return (
    <nav className="navbar">
        <h1 className="navbar-title">Import Manager</h1>
        <ul className="navbar-list">
     
            <li className="navbar-list__item navbar-list__item--right">
              <a className="navbar-logout-link" onClick={handleLogout}>
                Logout
              </a>
            </li>
            <li className="navbar-list__item navbar-list__item--right">
              {currentUser && (
                <NavLink className={({isActive}) => (isActive ? "selected" : "")} to="/profile" end>
                  Profile
                </NavLink>
              )}
            </li>
            <li className="navbar-list__item navbar-list__item--right"> 
              <NavLink className={({isActive}) => (isActive ? "selected" : "")} to="/imports" end> 
                Imports 
              </NavLink> 
            </li>
            <li className="navbar-list__item navbar-list__item--right"> 
              <NavLink className={({isActive}) => (isActive ? "selected" : "")} to="/connections" end> 
                Connections 
              </NavLink> 
            </li>
        </ul>
    </nav>
  )
}

export default Navbar


