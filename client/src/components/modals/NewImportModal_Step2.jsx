import { useState, useEffect, useRef, useMemo } from 'react'
import { useLocation } from 'react-router'
import PropTypes from 'prop-types';
import Button from '../ui/Button';
import TableNavbar from '../ui/TableNavbar';
import TableHat from '../ui/TableHat';
import TableImport from '../ui/TableImport';

// Step 2: Select a HubSpot List.
function NewImportModal_Step2({setModalIsOpen, hubspotConnectionId, onListSelect, onBack}) {

 const state = useLocation()?.state;
 const connectionId = hubspotConnectionId || state?.hubspotConnectionId;
 if (connectionId) {
   sessionStorage.setItem("hubspotConnectionId", connectionId);
 }
 const [data, setData] = useState([]);
 const [loading, setLoading] = useState(true);
 const selectedHubspotListRef = useRef("");

 const headers = ["Name", "List Size", "Type", "Object", "Last Updated"];


 // Get HubSpot lists
  useEffect(() => {
    const controller = new AbortController();
    const signal = controller.signal;
    console.log("Requesting HubSpot lists...");

    const fetchData = async () => {
      setLoading(true);
      try {
        const response = await fetch("http://localhost:8080/hubspot-lists", {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: connectionId,
          signal
        });
 
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        const result = await response.json();
        setData(result);
        console.log("HubSpot lists retrieved:", result)
      } catch (error) {
        console.log("Failed to retrieve HubSpot Lists. " + error);
        alert("Failed to retrieve HubSpot Lists.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();

    return () => {
      controller.abort(); 
    };
  }, [connectionId]);

  const select = (selectedIds) => {
    if (selectedIds && selectedIds.length > 0) {
      selectedHubspotListRef.current = selectedIds[0];
      console.log(`Selected HubSpot list id: ${selectedHubspotListRef.current}`);
    }
  }

  // Transform API data to TableImport format
  const transformedData = useMemo(() => {
    return data.map(record => ({
      id: record.listId,
      cells: [record.name, record.listSize, record.processingType, record.objectTypeId, record.lastUpdated]
    }));
  }, [data]);
  
  return (
    <div style={{backgroundColor: '#2d3e50'}}>
      
      <TableHat title="Select a HubSpot List" loading={loading} onClose={() => setModalIsOpen(false)} />

      <TableImport
        headers={headers}
        data={transformedData}
        useRadio={true}
        onSelectionChange={select}
      />

      <TableNavbar>
        <Button onClick={() => onBack()}>Back</Button>
        <Button onClick={() => onListSelect(selectedHubspotListRef.current)}>Next</Button>
      </TableNavbar>
    
    </div>
  )
}

NewImportModal_Step2.propTypes = {
  modalIsOpen: PropTypes.bool,
  setModalIsOpen: PropTypes.func,
  hubspotConnectionId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onListSelect: PropTypes.func,
  onBack: PropTypes.func
}

export default NewImportModal_Step2

