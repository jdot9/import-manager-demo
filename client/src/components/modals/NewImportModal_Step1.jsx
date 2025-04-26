import { useState, useEffect, useRef, useMemo } from 'react'
import PropTypes from 'prop-types';
import Button from '../ui/Button';
import TableNavbar from '../ui/TableNavbar';
import TableHat from '../ui/TableHat';
import TableImport from '../ui/TableImport';

// Step 1: Select a HubSpot Connection.
function NewImportModal_Step1({setModalIsOpen, onConnectionSelect}) {

const [data, setData] = useState([]);
const [loading, setLoading] = useState(true);
const selectedConnectionRef = useRef();

const headers = ["Name", "Status"];

const setConnection = (selectedIds) => {
  if (selectedIds && selectedIds.length > 0) {
    selectedConnectionRef.current = selectedIds[0];
    sessionStorage.setItem("hubspotConnectionId", selectedConnectionRef.current)
  }
}

// Transform API data to TableImport format
const transformedData = useMemo(() => {
  return data.map(record => ({
    id: record.connectionId,
    cells: [record.connectionName, record.connectionStatus]
  }));
}, [data]);

// Get HubSpot Connections from database
useEffect(() => {
  const controller = new AbortController(); 
  const signal = controller.signal;

  const fetchData = async () => {
    setLoading(true);
    console.log("Getting HubSpot connections.")
    try {
      const response = await fetch("http://localhost:8080/hubspot-connections", { signal });

      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      
      const data = await response.json();
      setData(data);
      console.log("HubSpot connections retrieved.")
    } catch (error) {
        console.log(error);
        alert("Failed to retrieve HubSpot connections.");
    } finally {
      setLoading(false);
    }
  };

  fetchData();

  return () => {
    controller.abort();
  };
}, []);

  return (
    <div style={{backgroundColor: '#2d3e50'}}>

      <TableHat title="Select a HubSpot Connection" loading={loading} onClose={() => setModalIsOpen(false)} />

      <TableImport
        headers={headers}
        data={transformedData}
        useRadio={true}
        onSelectionChange={setConnection}
      />

      <TableNavbar>
        <Button onClick={() => onConnectionSelect(selectedConnectionRef.current)}>Next</Button>
      </TableNavbar>

    </div>
  )
}

NewImportModal_Step1.propTypes = {
  setModalIsOpen: PropTypes.func,
  onConnectionSelect: PropTypes.func
}

export default NewImportModal_Step1

