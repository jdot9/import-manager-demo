const API_BASE_URL = 'http://localhost:8080/api';

class ApiService {

  /**
   * Get all API types
   * @returns {Promise<Array>} List of API types
   */
  async getAllApiTypes() {
    try {
      const response = await fetch(`${API_BASE_URL}/api-types`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch API types');
      }

      const data = await response.json();
      console.log('API Types retrieved:', data);
      return data;
    } catch (error) {
      console.error('Error fetching API types:', error);
      throw error;
    }
  }

  /**
   * Get all API authentication types
   * @returns {Promise<Array>} List of API auth types
   */
  async getAllApiAuthTypes() {
    try {
      const response = await fetch(`${API_BASE_URL}/api-auth-types`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch API auth types');
      }

      const data = await response.json();
      console.log('API Auth Types retrieved:', data);
      return data;
    } catch (error) {
      console.error('Error fetching API auth types:', error);
      throw error;
    }
  }

  /**
   * Get all API REST methods (HTTP methods)
   * @returns {Promise<Array>} List of HTTP methods
   */
  async getAllApiRestMethods() {
    try {
      const response = await fetch(`${API_BASE_URL}/api-rest-methods`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch API REST methods');
      }

      const data = await response.json();
      console.log('API REST Methods retrieved:', data);
      return data;
    } catch (error) {
      console.error('Error fetching API REST methods:', error);
      throw error;
    }
  }

  /**
   * Post a new API with optional logo file
   * @param {Object} apiData - API data
   * @param {File} logoFile - Optional logo file to upload
   * @returns {Promise<Object>} Response with created API
   */
  async postNewApi(apiData, logoFile = null) {
    try {
      let response;
      
      if (logoFile) {
        // Use multipart/form-data when logo file is provided
        const formData = new FormData();
        formData.append('data', new Blob([JSON.stringify(apiData)], { type: 'application/json' }));
        formData.append('logo', logoFile);
        
        response = await fetch(`${API_BASE_URL}/apis`, {
          method: 'POST',
          body: formData,
        });
      } else {
        // Use JSON when no logo file
        response = await fetch(`${API_BASE_URL}/apis`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(apiData),
        });
      }

      if (response.ok) {
        alert('API saved successfully');
      } else {
        const errorText = await response.text();
        alert('Error: ' + errorText);
      }
    } catch(error) {
      console.error('Error saving API:', error);
      alert('Error saving API: ' + error.message);
    }
  }

  /**
   * Get all APIs for a specific user
   * @param {number} userId - The user ID
   * @returns {Promise<Array>} List of APIs for the user
   */
  async getAllApisByUserUuid(userUuid) {
    try {
      const response = await fetch(`${API_BASE_URL}/apis?userId=${userUuid}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch APIs for user');
      }

      const data = await response.json();
      console.log('APIs retrieved for user:', data);
      return data;
    } catch (error) {
      console.error('Error fetching APIs for user:', error);
      throw error;
    }
  }

  /**
   * Get a specific API by UUID
   * @param {string} apiUuid - The API UUID
   * @returns {Promise<Object>} The API object
   */
  async getApiByUuid(apiUuid) {
    try {
      const response = await fetch(`${API_BASE_URL}/apis/api?apiId=${apiUuid}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch API');
      }

      const data = await response.json();
      console.log('API retrieved:', data);
      return data;
    } catch (error) {
      console.error('Error fetching API:', error);
      throw error;
    }
  }

  /**
   * Get all API endpoints for a specific API
   * @param {string} apiId - The API ID
   * @returns {Promise<Array>} List of API endpoints for the API
   */
  async getApiEndpointsByApiId(apiId) {
    try {
      const response = await fetch(`${API_BASE_URL}/apis/${apiId}/endpoints`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      });
      if (!response.ok) {
        throw new Error('Failed to fetch API endpoints');
      }

      const data = await response.json();
      console.log('API endpoints retrieved:', data);
      return data;
    } catch (error) {
      console.error('Error fetching API endpoints:', error);
      throw error;
    }
  }

  async deleteApiByUuid(apiUuid) {
    try {
      const response = await fetch(`${API_BASE_URL}/apis/${apiUuid}`, {
        method: 'DELETE'
      });
      
      if (!response.ok) {
        throw new Error("Failed to delete API.");
      }

     // 204 No Content → nothing to parse
      return true;
    
    } catch (error) {
      console.error('Error deleting API: ', error);
      throw error;
    }

  }
  
}


export default new ApiService();
