const API_BASE_URL = 'http://localhost:8080/api';

class UserService {
  /**
   * Helper method to handle fetch responses
   * @param {Response} response - Fetch response
   * @returns {Promise} Parsed JSON data
   */
  async handleResponse(response) {
    const data = await response.json().catch(() => ({}));
    
    if (!response.ok) {
      const error = new Error(data.message || 'Request failed');
      error.response = { data, status: response.status };
      throw error;
    }
    
    return data;
  }

  /**
   * Login user with email and password
   * @param {string} email - User's email
   * @param {string} password - User's password
   * @returns {Promise} Response with user data and token
   */
  async login(email, password) {
    try {
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password })
      });
      
      const data = await this.handleResponse(response);
      
      // Store user data and token in localStorage
      if (data.token) {
        // Store the complete user object including UUID
        const userData = {
          uuid: data.uuid,
          firstName: data.firstName,
          lastName: data.lastName,
          email: data.email,
          userRole: data.userRole
        };
        
        localStorage.setItem('user', JSON.stringify(userData));
        localStorage.setItem('token', data.token);
        
        console.log('User logged in successfully:', userData);
      }
      
      return data;
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  }

  /**
   * Register a new user
   * @param {Object} userData - User registration data
   * @returns {Promise} Response with created user
   */
  async register(userData) {
    try {
      const response = await fetch(`${API_BASE_URL}/users`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData)
      });
      
      return await this.handleResponse(response);
    } catch (error) {
      console.error('Registration error:', error);
      throw error;
    }
  }

  /**
   * Logout user
   */
  logout() {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  }

  /**
   * Get current logged in user
   * @returns {Object|null} Current user or null
   */
  getCurrentUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  /**
   * Check if user is authenticated
   * @returns {boolean} True if user is logged in
   */
  isAuthenticated() {
    return localStorage.getItem('token');
  }

  /**
   * Get user by UUID
   * @param {string} uuid - User UUID
   * @returns {Promise} User data
   */
  async getUserByUUID(uuid) {
    try {
      const response = await fetch(`${API_BASE_URL}/users/${uuid}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      });
      
      return await this.handleResponse(response);
    } catch (error) {
      console.error('Get user error:', error);
      throw error;
    }
  }

  /**
   * Update user by UUID
   * @param {string} uuid - User UUID
   * @param {Object} userData - Updated user data
   * @returns {Promise} Updated user data
   */
  async updateUser(uuid, userData) {
    try {
      const response = await fetch(`${API_BASE_URL}/users/${uuid}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData)
      });
      
      return await this.handleResponse(response);
    } catch (error) {
      console.error('Update user error:', error);
      throw error;
    }
  }

  /**
   * Get all users
   * @returns {Promise} List of all users
   */
  async getAllUsers() {
    try {
      const response = await fetch(`${API_BASE_URL}/users`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      });
      
      return await this.handleResponse(response);
    } catch (error) {
      console.error('Get all users error:', error);
      throw error;
    }
  }
}

export default new UserService();

