import Card from '../ui/Card'
import { useEffect, useState } from 'react'
import Modal from 'react-modal'
import PropTypes from 'prop-types'
import nologo from '../../assets/nologo.jpg'
import Button from '../ui/Button';
import TableNavbar from '../ui/TableNavbar'
import Connections from '../Connections'
import Connection from '../Connection'
import SelectedConnectionModal from './SelectedConnectionModal'
import hubspotlogo from '../../assets/Hubspot-Logo.jpg'
import five9logo from '../../assets/Five9-Logo.jpg'

function ConnectionTypesModal({ modalIsOpen, setModalIsOpen }) {

  const [step, setStep] = useState(0);
  const [selectedConnection, setSelectedConnection] = useState("");

  const user = JSON.parse(localStorage.getItem("user"));
  console.log("user.uuid", user.uuid)
  
  
  const handleConnectionChange = (connectionValue) => {
    setSelectedConnection(connectionValue);
    console.log('Selected connection:', connectionValue);
  };
 
  const handleCloseModal = () => {

    setStep(0);
    setModalIsOpen(false);
  };

  return (
    <Modal
       isOpen={modalIsOpen}
       onRequestClose={handleCloseModal}
       style={{
         overlay: {
           backgroundColor: 'rgba(0, 0, 0, 0.75)',
           zIndex: '100'
         },
         content: {
           width: '100%',
           height: '100%',
           margin: 'auto',
           border: 'none',
           backgroundColor: 'transparent'
         }
       }}
       contentLabel="Connections Modal"
    >
      
    {step === 0 && (  
      <Card closeModal={handleCloseModal}>
        <h1 style={{textAlign: 'center'}}>Select a connection type</h1>

        <Connections>
          <Connection logo={hubspotlogo} name="HubSpot" value="hubspot" checked={selectedConnection === "hubspot"} onChange={handleConnectionChange} />
          <Connection logo={five9logo} name="Five9" value="five9" checked={selectedConnection === "five9"} onChange={handleConnectionChange} />
        </Connections>

        <TableNavbar> 
             <Button onClick={() => setStep(3)} disabled={!selectedConnection}>Open</Button>    
        </TableNavbar>
      </Card>
    )}

    {step === 3 && (
      <SelectedConnectionModal 
        selectedConnection={selectedConnection}
        onBack={() => setStep(0)}
        userUuid={user.uuid}
      />
    )}
    </Modal>
  );
}

ConnectionTypesModal.propTypes = {
  modalIsOpen: PropTypes.bool.isRequired,
  setModalIsOpen: PropTypes.func.isRequired
}

export default ConnectionTypesModal;
