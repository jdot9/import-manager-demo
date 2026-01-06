import { useEffect, useRef, useState, useMemo } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'

import Table from '../components/ui/Table';

import Button from '../components/ui/Button'
import Toolbar from '../components/ui/Toolbar';
import Dropdown from '../components/ui/Dropdown';
import ConnectionTypesModal from '../components/modals/ConnectionTypesModal';
import UserService from '../services/UserService';
import ConnectionService from '../services/ConnectionService';
import hubspotLogo from '../assets/Hubspot-Logo.jpg';
import five9Logo from '../assets/Five9-Logo.jpg';
import noLogo from '../assets/nologo.jpg';

function ConnectionPage() {

const [searchParams, setSearchParams] = useSearchParams();
const navigate = useNavigate();
const headers = ["Type", "Name", "Created", "Description", "Status"]; 
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

// Helper function to get the appropriate logo based on connection data
const logoStyle = { width: '100%', height: '60px', objectFit: 'contain', display: 'block' };

const getConnectionLogo = (record) => {
  if (record.hubspotAccessToken) {
    return <img src={hubspotLogo} alt="HubSpot" style={logoStyle} />;
  } else if (record.five9Username) {
    return <img src={five9Logo} alt="Five9" style={logoStyle} />;
  }
  return <img src={noLogo} alt="Unknown" style={logoStyle} />;
};

// Transform data to Table component format
const transformedData = useMemo(() => {
  return data.map(record => ({
    id: record.id,
    cells: [
      getConnectionLogo(record),
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
    console.log('OAuth user UUID:', uuid);
    
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

// Function to refresh connections from database
const refreshConnections = () => {
  setLoading(true);
  ConnectionService.getConnectionsForUserByUuid(user.uuid, setLoading).then(d => setData(d));
};

// Function to delete selected connections
const handleDeleteConnections = async () => {
  if (selectedFromChild.length === 0) {
    alert('Please select connections to delete');
    return;
  }
  
  const confirmDelete = window.confirm(`Are you sure you want to delete ${selectedFromChild.length} connection(s)?`);
  if (!confirmDelete) return;
  
  const result = await ConnectionService.deleteConnections(selectedFromChild);
  if (result.successCount > 0) {
    refreshConnections();
    setSelectedFromChild([]);
  }
};

// Get connections from database on mount
useEffect(() => { refreshConnections() }, [user.uuid]);

  return (
    
      <>

          {/* Toolbar for Creating and DeletingConnections */}
          <Toolbar title={(loading) ? "Loading Connections..." : `Connections: ${data.length}`}>
              <Button style={{float: 'right'}} 
                      onClick={handleDeleteConnections}> - Delete Connection</Button>

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
   
          {connectionModalIsOpen && <ConnectionTypesModal modalIsOpen={connectionModalIsOpen} setModalIsOpen={setConnectionModalIsOpen} onConnectionSaved={refreshConnections} />}
      </>
  )
}

export default ConnectionPage
