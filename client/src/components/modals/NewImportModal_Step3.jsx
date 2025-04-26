import { useState, useEffect, useRef, useMemo } from "react";
import { useLocation } from "react-router";
import PropTypes from 'prop-types';
import Button from '../ui/Button';
import TableNavbar from '../ui/TableNavbar';
import TableHat from '../ui/TableHat';
import TableImport from '../ui/TableImport';

// Step 3: Select a Five9 Connection.
function NewImportModal_Step3({setModalIsOpen, hubspotConnectionId, hubspotListId, onConnectionSelect, onBack}) {

 const headers = ["Name","Status"];
 const [data, setData] = useState([]);
 const [loading, setLoading] = useState(true);
 const selectedConnectionRef = useRef();
 const state = useLocation()?.state;
 
 const hsConnectionId = hubspotConnectionId || state?.hubspotConnectionId;
 const hsListId = hubspotListId || state?.hubspotListId;
 
 console.log(`HubSpot Connection id: ${hsConnectionId}`);
 console.log(`HubSpot List id: ${hsListId}`);
 
 const select = (selectedIds) => { 
  if (selectedIds && selectedIds.length > 0) {
    selectedConnectionRef.current = selectedIds[0];
  }
}

// Transform API data to TableImport format
const transformedData = useMemo(() => {
  return data.map(record => ({
    id: record.connectionId,
    cells: [record.connectionName, record.connectionStatus]
  }));
}, [data]);
  // Get Five9 Connections from database
  //useEffect(() => getFive9Connections(setData, setLoading, setError), []);
  useEffect(() => {
    const controller = new AbortController();
    const signal = controller.signal;
    const fetchData = async () => {
      setLoading(true);
      console.log("Getting Five9 Connections.");
      try {
        const response = await fetch("http://localhost:8080/five9-connections", {signal});


        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        
        const data = await response.json();
        setData(data);
        console.log("Five9 connections retrieved.")

      } catch (error) {
        console.log(error);
        alert("Failed to retrieve Five9 connections.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();

    return () => {
      controller.abort()
    };

  }, [])

  return (
    <div style={{backgroundColor: '#2d3e50'}}>
      
      <TableHat title="Select a Five9 VCC Connection" loading={loading} onClose={() => setModalIsOpen(false)} />

      <TableImport
        headers={headers}
        data={transformedData}
        useRadio={true}
        onSelectionChange={select}
      />

      <TableNavbar>
        <Button onClick={() => onBack()}>Back</Button>
        <Button onClick={() => onConnectionSelect(selectedConnectionRef.current)}>Next</Button>
      </TableNavbar>
        
    </div>
  )
}

NewImportModal_Step3.propTypes = {
  modalIsOpen: PropTypes.bool,
  setModalIsOpen: PropTypes.func,
  hubspotConnectionId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  hubspotListId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onConnectionSelect: PropTypes.func,
  onBack: PropTypes.func
}

export default NewImportModal_Step3

