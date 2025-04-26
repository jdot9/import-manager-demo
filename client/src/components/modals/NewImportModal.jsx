import Modal from 'react-modal'
import { useState } from 'react'
import PropTypes from 'prop-types'
import NewImportModal_Step1 from './NewImportModal_Step1'
import NewImportModal_Step2 from './NewImportModal_Step2'
import NewImportModal_Step3 from './NewImportModal_Step3'
import NewImportModal_Step4 from './NewImportModal_Step4'
import NewImportModal_Step5 from './NewImportModal_Step5'
import NewImportModal_Step6 from './NewImportModal_Step6'

/* Multi Step Import Modal */

function NewImportModal({modalIsOpen, setModalIsOpen}) {
    
    const [step, setStep] = useState(1);
    const [hubspotConnectionId, setHubspotConnectionId] = useState(null);
    const [hubspotListId, setHubspotListId] = useState(null);
    const [five9ConnectionId, setFive9ConnectionId] = useState(null);
    const [five9DialingListId, setFive9DialingListId] = useState(null);

    const handleHubspotConnectionSelect = (connectionId) => {
        setHubspotConnectionId(connectionId);
        setStep(2);
    }
    
    const handleHubspotListSelect = (listId) => {
        setHubspotListId(listId);
        setStep(3);
    }
    
    const handleFive9ConnectionSelect = (connectionId) => {
        setFive9ConnectionId(connectionId);
        setStep(4);
    }
    
    const handleFive9DialingListSelect = (dialingListId) => {
        setFive9DialingListId(dialingListId);
        setStep(5);
    }
    
  return (
    <Modal
    isOpen={modalIsOpen}
    onRequestClose={() => setModalIsOpen(false)}
    style={{
      overlay: {
        backgroundColor: 'rgba(0, 0, 0, 0.75)',
        zIndex: '100'
      },
      content: {
        width: '90%',
        height: '90%',
        margin: 'auto',
        border: 'none',
        backgroundColor: 'transparent'
      }
    }}
    contentLabel="New Import Modal"
  >
    {step === 1 && (
      <NewImportModal_Step1 
        modalIsOpen={modalIsOpen} 
        setModalIsOpen={setModalIsOpen}
        onConnectionSelect={handleHubspotConnectionSelect}
      />
    )}
    {step === 2 && (
      <NewImportModal_Step2
        modalIsOpen={modalIsOpen}
        setModalIsOpen={setModalIsOpen}
        hubspotConnectionId={hubspotConnectionId}
        onListSelect={handleHubspotListSelect}
        onBack={() => setStep(1)}
      />
    )}
    {step === 3 && (
      <NewImportModal_Step3
        modalIsOpen={modalIsOpen}
        setModalIsOpen={setModalIsOpen}
        hubspotConnectionId={hubspotConnectionId}
        hubspotListId={hubspotListId}
        onConnectionSelect={handleFive9ConnectionSelect}
        onBack={() => setStep(2)}
      />
    )}
    {step === 4 && (
      <NewImportModal_Step4
        modalIsOpen={modalIsOpen}
        setModalIsOpen={setModalIsOpen}
        hubspotConnectionId={hubspotConnectionId}
        hubspotListId={hubspotListId}
        five9ConnectionId={five9ConnectionId}
        onDialingListSelect={handleFive9DialingListSelect}
        onBack={() => setStep(3)}
      />
    )}
    {step === 5 && (
      <NewImportModal_Step5
        modalIsOpen={modalIsOpen}
        setModalIsOpen={setModalIsOpen}
        hubspotConnectionId={hubspotConnectionId}
        hubspotListId={hubspotListId}
        five9ConnectionId={five9ConnectionId}
        five9DialingListId={five9DialingListId}
        onBack={() => setStep(4)}
        onComplete={() => setStep(6)}
      />
    )}
    {step === 6 && (
      <NewImportModal_Step6
        modalIsOpen={modalIsOpen}
        setModalIsOpen={setModalIsOpen}
        hubspotConnectionId={hubspotConnectionId}
        hubspotListId={hubspotListId}
        five9ConnectionId={five9ConnectionId}
        five9DialingListId={five9DialingListId}
        onBack={() => setStep(5)}
        onSave={(scheduleData) => {
          console.log('Import saved with:', {
            hubspotConnectionId,
            hubspotListId,
            five9ConnectionId,
            five9DialingListId,
            schedule: scheduleData
          });
          alert('Import scheduled successfully!');
          setModalIsOpen(false);
        }}
      />
    )}
  </Modal>
  )
}

NewImportModal.propTypes = {
  modalIsOpen: PropTypes.bool.isRequired,
  setModalIsOpen: PropTypes.func.isRequired
}

export default NewImportModal

