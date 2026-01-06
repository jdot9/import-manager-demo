const API_BASE_URL = 'http://localhost:8080/api';

class ConnectionService {

async saveConnection(connection) {
  try {
    console.log('Saving connection:', JSON.stringify(connection, null, 2));
    const response = await fetch(`${API_BASE_URL}/connections`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(connection)
    });

    if (response.ok) {
      alert('Connection saved successfully');
      return true;
    } else {
      const errorText = await response.text();
      alert('Error: ' + errorText);
      return false;
    }
  } catch (error) {
    console.error('Error saving connection:', error);
    alert('Error saving connection: ' + error.message);
    return false;
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

    async deleteConnections(connectionIds) {
        if (!connectionIds || connectionIds.length === 0) {
            console.log('No connections selected for deletion');
            return { success: false, message: 'No connections selected' };
        }

        console.log('Deleting connections:', connectionIds);
        const results = [];

        for (const id of connectionIds) {
            try {
                const response = await fetch(`${API_BASE_URL}/connections/${id}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });

                if (response.ok) {
                    results.push({ id, success: true });
                } else {
                    const errorText = await response.text();
                    results.push({ id, success: false, error: errorText });
                }
            } catch (error) {
                console.error(`Error deleting connection ${id}:`, error);
                results.push({ id, success: false, error: error.message });
            }
        }

        const successCount = results.filter(r => r.success).length;
        const failCount = results.filter(r => !r.success).length;

        if (failCount === 0) {
            alert(`Successfully deleted ${successCount} connection(s)`);
        } else if (successCount === 0) {
            alert(`Failed to delete ${failCount} connection(s)`);
        } else {
            alert(`Deleted ${successCount} connection(s), failed to delete ${failCount}`);
        }

        return { 
            success: failCount === 0, 
            results,
            successCount,
            failCount
        };
    }
}

export default new ConnectionService();