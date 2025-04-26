import { useState } from 'react'

import Toggle from '../components/ui/Toggle'
import NewImportModal from '../components/modals/NewImportModal'
import Button from '../components/ui/Button'
import Toolbar from '../components/ui/Toolbar'



function ImportPage() {

  // Select all checkboxes when checked
  const checkboxAll = <input type="checkbox" />
  const headers = [checkboxAll, "Name", "Created", "Modified", "Status", "Next Scheduled Run", "Stop/Start"]

  // Fetch Data Results
  const [data] = useState([]);

  const [modalIsOpen, setModalIsOpen] = useState(false);

  sessionStorage.setItem("hubspotConnectionId", "");
  sessionStorage.setItem("hubspotListId", "");
  sessionStorage.setItem("five9ConnectionId", "");
  sessionStorage.setItem("five9DialingListId", "");

  return (

    <>
         <Toolbar title={`Imports: ${data.length}`}>
             <Button style={{float: 'right'}} onClick={() => setModalIsOpen(true)}>- Delete Import</Button>
             <Button style={{float: 'right'}} onClick={() => setModalIsOpen(true)}>+ New Import</Button>
         </Toolbar>

      {modalIsOpen && <NewImportModal modalIsOpen={modalIsOpen} setModalIsOpen={setModalIsOpen} />}

      <table>
              <thead>       
                  <tr>
                    {headers.map((header, index) => (
                      <th key={index}>{header}</th>
                    ))}
                  </tr>
              </thead>
              <tbody>
                      {data.map(record => (
                      <tr key={record.connectionId}>
                        <td><input type="checkbox"/></td>
                        <td>{record.connectionType}</td>
                        <td>{record.connectionName}</td>
                        <td>{record.dateCreated}</td>
                        <td>{record.dateModified}</td>
                        <td>{record.connectionStatus}</td>
                        <td><Toggle /></td>
                      </tr>
                    ))}
              </tbody>
          </table>
  

    </>
  )
}

export default ImportPage
