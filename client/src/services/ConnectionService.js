const API_BASE_URL = 'http://localhost:8080/api';

class ConnectionService {

async saveConnection(connection) {
  try {
    console.log(connection.user_uuid);
    const response = await fetch(`${API_BASE_URL}/connections`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(connection)
    });

    if (response.ok) {
      alert('Connection saved successfully');
    } else {
      const errorText = await response.text();
      alert('Error: ' + errorText);
    }
  } catch (error) {
    console.error('Error saving connection:', error);
    alert('Error saving connection: ' + error.message);
  }
}


    async getConnectionsForUserByUuid(userUuid, setLoading) {
        try {
            const response = await fetch(`${API_BASE_URL}/connections?userId=${userUuid}`,{
                method: 'GET',
                headers: {
                    'Content-Type':'application/json'
                }
            });

        if (!response.ok) {
            throw new Error('Failed to fetch connections for user');
        }
            const data = await response.json();
            console.log('Connections retrieved for user:', data);
            setLoading(false);
            return data;
        } catch (error) {
            console.error('Error fetching connections for user:', error);
            throw error;
        }
    }



}

export default new ConnectionService();