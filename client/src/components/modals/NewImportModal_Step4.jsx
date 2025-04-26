import { useState, useEffect, useRef, useMemo } from "react";
import { useLocation } from "react-router";
import PropTypes from 'prop-types';
import Button from '../ui/Button';
import TableNavbar from '../ui/TableNavbar';
import TableHat from '../ui/TableHat';
import TableImport from '../ui/TableImport';

// Step 4: Select a Five9 Dialing List.
function NewImportModal_Step4({setModalIsOpen, five9ConnectionId, onDialingListSelect, onBack}) {
   const headers = ["Name", "List Size"];
   const [data, setData] = useState([]);
   const [loading, setLoading] = useState(true);
   const selectedDialingListRef = useRef();
   const state = useLocation()?.state;
   
   const f9ConnectionId = five9ConnectionId || state?.five9ConnectionId;
   
   const select = (selectedIds) => {
     if (selectedIds && selectedIds.length > 0) {
       selectedDialingListRef.current = selectedIds[0];
       console.log(`Selected Five9 dialing list: ${selectedDialingListRef.current}`);
     }
   };

   // Transform API data to TableImport format
   const transformedData = useMemo(() => {
     return data.map((record, index) => ({
       id: record[0] || index,
       cells: [record[0], record[1]]
     }));
   }, [data]);
       
  // Get Dialing List from Five9 Configuration Web Services API 
  // useEffect((event) => getDialingList2(event, setData, setLoading, setError),[]);
useEffect(() => {
  const controller = new AbortController();

  const fetchData = async () => {
    try {
      const response = await fetch("http://localhost:8080/five9-dialing-lists", {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: f9ConnectionId,
        signal: controller.signal // Attach the abort signal here
      });

      if (response.ok) {
        let result = await response.text();
        result = result.split(",").map(item => item.replace(/[{}:]/g, ""))
                                  .map(item => item.replace(/["]/g, " ").trim())
                                  .map(item => item.split(/ (?=\d)/));
        setData(result); 
        console.log(result)
        setLoading(false);
      } else {
        alert("Error: " + response.statusText);
      }
    } catch (error) {
      if (error.name === 'AbortError') {
        console.log('Fetch aborted');
      } else {
        console.error("Error:", error);
      }
    }
  };

  fetchData();

  return () => {
    controller.abort(); // Clean up by aborting fetch if component unmounts
  };
}, [f9ConnectionId]);

  return (
    <div style={{backgroundColor: '#2d3e50'}}>
      
      <TableHat title="Select a Five9 VCC Dialing List" loading={loading} onClose={() => setModalIsOpen(false)} />

      <TableImport
        headers={headers}
        data={transformedData}
        useRadio={true}
        onSelectionChange={select}
      />

      <TableNavbar>
        <Button onClick={() => onBack()}>Back</Button>
        <Button onClick={() => onDialingListSelect(selectedDialingListRef.current)}>Next</Button>
      </TableNavbar>
        
    </div>
  )
}

NewImportModal_Step4.propTypes = {
  modalIsOpen: PropTypes.bool,
  setModalIsOpen: PropTypes.func,
  hubspotConnectionId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  hubspotListId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  five9ConnectionId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onDialingListSelect: PropTypes.func,
  onBack: PropTypes.func
}

export default NewImportModal_Step4

