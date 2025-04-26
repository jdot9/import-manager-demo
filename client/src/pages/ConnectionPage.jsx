import { useEffect, useRef, useState, useMemo } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'

import Table from '../components/ui/Table';
import { getConnections, deleteConnections } from '../services/ConnectionClient';
import Button from '../components/ui/Button'
import Toolbar from '../components/ui/Toolbar';
import Dropdown from '../components/ui/Dropdown';
import ConnectionTypesModal from '../components/modals/ConnectionTypesModal';
import UserService from '../services/UserService';
import ConnectionService from '../services/ConnectionService';

function ConnectionPage() {

const [searchParams, setSearchParams] = useSearchParams();
const navigate = useNavigate();
const headers = ["Type", "Name", "Created", "Endpoint", "Status"]; 
const connectionTypeRef = useRef(null); 
const [modalIsOpen, setModalIsOpen] = useState(false); 
const [connectionModalIsOpen, setConnectionModalIsOpen] = useState(false);
const connections = useRef([]); 
const connectionSelectedRef = useRef(false); 

// Results for getting connections from database
const [data, setData] = useState([]);
const [loading, setLoading] = useState(true); 
const [error, setError] = useState(null);

const [selectedFromChild, setSelectedFromChild] = useState([]);

const handleSelectedChange = (selectedIds) => {
  setSelectedFromChild(selectedIds);
  console.log("Connection IDs selected:", selectedIds);
};

// Transform data to Table component format
const transformedData = useMemo(() => {
  return data.map(record => ({
    id: record.id,
    cells: [
      record.connectionType,
      record.name,
      record.createdAt,
      record.description,
      record.status
    ]
  }));
}, [data]);

// Handle OAuth2 callback
useEffect(() => {
  const oauth = searchParams.get('oauth');
  const uuid = searchParams.get('uuid');
  const email = searchParams.get('email');

  if (oauth === 'success' && uuid && email) {
    console.log('Processing OAuth2 login callback...');
    
    const fetchOAuthUser = async () => {
      try {
        const userData = await UserService.getUserByUUID(uuid);
        
        // Store user data in localStorage
        const userSession = {
          uuid: userData.uuid,
          firstName: userData.firstName,
          lastName: userData.lastName,
          email: userData.email,
          userRole: userData.userRole?.role || null
        };
        
        localStorage.setItem('user', JSON.stringify(userSession));
        localStorage.setItem('token', uuid);
        
        console.log('OAuth2 user logged in successfully:', userSession);
   
        
        // Clean up URL params by navigating to clean connections page
        setSearchParams({});
      } catch (err) {
        console.error('Error fetching OAuth user:', err);
        navigate('/login?error=oauth_failed');
      }
    };

    fetchOAuthUser();
  }
}, [searchParams, setSearchParams, navigate]);

  const user = JSON.parse(localStorage.getItem('user'));
  console.log(user.uuid);

// Get connections from database
//useEffect(() => getConnections(setData, setLoading, setError, connections, connectionSelectedRef), []);
useEffect(() => { ConnectionService.getConnectionsForUserByUuid(user.uuid, setLoading).then(d => setData(d)) }, [user.uuid]);

//alert(data[0].type);

  return (
    
      <>

          {/* Toolbar for Creating and DeletingConnections */}
          <Toolbar title={(loading) ? "Loading Connections..." : `Connections: ${data.length}`}>
              <Button style={{float: 'right'}} 
                      onClick={(event) => deleteConnections(event, selectedFromChild)}> - Delete Connection</Button>

              <Button style={{float: 'right'}} onClick={() => setConnectionModalIsOpen(true)}>+ New Connection</Button>
          </Toolbar>

          {/* Table displays Connections retrieved from database */}
          <Table 
            headers={headers} 
            data={transformedData} 
            useCheckbox={true}
            useToggle={true}
            onSelectionChange={handleSelectedChange} 
          />
   
          {connectionModalIsOpen && <ConnectionTypesModal modalIsOpen={connectionModalIsOpen} setModalIsOpen={setConnectionModalIsOpen} />}
      </>
  )
}

export default ConnectionPage
