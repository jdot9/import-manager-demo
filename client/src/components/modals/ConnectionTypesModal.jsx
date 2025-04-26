import Card from '../ui/Card'
import { useEffect, useState } from 'react'
import Modal from 'react-modal'
import PropTypes from 'prop-types'
import nologo from '../../assets/nologo.jpg'
import Button from '../ui/Button';
import TableNavbar from '../ui/TableNavbar'
import Connections from '../Connections'
import Connection from '../Connection'
import NewConnectionTypeModal_Step1 from './NewConnectionTypeModal_Step1'
import NewConnectionTypeModal_Step2 from './NewConnectionTypeModal_Step2'
import ApiService from '../../services/ApiService'
import SelectedConnectionModal from './SelectedConnectionModal'

function ConnectionTypesModal({ modalIsOpen, setModalIsOpen }) {

  const defaultFormData = {
  apiType: "",
  authType: "",
  authDetails: "",
  apiName: "",
  baseUrl: "",
  name: "",
  path: "",
  httpMethod: "",
  headers: "",
  queryParameters: "",
  requestBody: "",
  soapEnvelope: "",
  soapAction: "",
  description: ""
};

    const zeroConnectionsMessage = (
        <div style={{display: 'block'}}>
          <h2 style={{textAlign: 'left'}}>No connection types found.</h2>
          <h3>Click Custom Integration to add a new connection type.</h3>
        </div>
      );

  const [apiEndpoints, setApiEndpoints] = useState([]);
  const [step, setStep] = useState(0);
  const [selectedConnection, setSelectedConnection] = useState("");
  const [formData, setFormData] = useState(defaultFormData);
  const [logoFile, setLogoFile] = useState(null);
  const [types, setTypes] = useState([]);

  const user = JSON.parse(localStorage.getItem("user"));
  console.log("user.uuid", user.uuid)
  
    useEffect(() => {
      ApiService.getAllApisByUserUuid(user.uuid)
        .then(data => setTypes(data));
    }, [user.uuid]);

  const handleChange = (event) => {
    const { name, value, type, files } = event.target;
    
    // Handle file inputs specially
    if (type === 'file') {
      if (files && files[0]) {
        setLogoFile(files[0]);
        console.log('Logo file selected:', files[0].name);
      }
    } else {
      setFormData({ ...formData, [name]: value });
    }
  };

  const handleConnectionChange = (connectionValue) => {
    setSelectedConnection(connectionValue);
    console.log('Selected connection:', connectionValue);
  };


  const handleFinish = async () => {
    // Validate endpoints before submission
    if (apiEndpoints.length === 0) {
      alert('Please add at least one API endpoint before finishing.');
      return;
    }
    
    // Validate each endpoint has a valid HTTP method
    for (let i = 0; i < apiEndpoints.length; i++) {
      const endpoint = apiEndpoints[i];
      if (!endpoint.httpMethod || endpoint.httpMethod.trim() === '') {
        alert(`Endpoint "${endpoint.name || i + 1}" is missing an HTTP method.`);
        return;
      }
    }
    
    // Prepare data for API - convert empty strings to null and parse JSON strings
    const step1_preparedData = {
      userUuid: user.uuid, // User UUID from current session
      apiName: formData.apiName || null,
      baseUrl: formData.baseUrl || null,
      apiTypeId: parseInt(formData.apiType),
      apiAuthTypeId: parseInt(formData.authType),
      authDetails: formData.authDetails && formData.authDetails.trim() !== "" ? JSON.parse(formData.authDetails) : null,
      logoUrl: formData.logoUrl || null,
    };

    const step2_preparedData = apiEndpoints.map(endpoint => {
      const methodId = parseInt(endpoint.httpMethod);
      return {
        name: endpoint.name || null,
        path: endpoint.path || null,
        apiRestMethodId: isNaN(methodId) ? null : methodId, 
        headers: endpoint.headers && endpoint.headers.trim() !== "" ? JSON.parse(endpoint.headers) : null,
        queryParameters: endpoint.queryParameters && endpoint.queryParameters.trim() !== "" ? JSON.parse(endpoint.queryParameters) : null,
        requestBody: endpoint.requestBody || null,
        soapEnvelope: endpoint.soapEnvelope || null,
        soapAction: endpoint.soapAction || null,
        description: endpoint.description || null
      }
    });

    const preparedData = {
      ...step1_preparedData,
      apiEndpoints: step2_preparedData
    };
    
    // Send POST request to API with logo file
    ApiService.postNewApi(preparedData, logoFile);
    
    // Clear all form data, logo file, and endpoints
    setFormData(defaultFormData);
    setLogoFile(null);
    setApiEndpoints([]);

    // Reset to step 0
    setStep(0);
  };

  const handleCloseModal = () => {
    // Reset all state when modal is closed
    setFormData(defaultFormData);
    setLogoFile(null);
    setApiEndpoints([]);
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
          {types.length == 0 && zeroConnectionsMessage}

          {types.map((api) => {
            const logoSrc = api.logoUrl 
              ? `http://localhost:8080/api/logos/${api.logoUrl.replace('Logos/', '')}`
              : nologo;
            return (
              <Connection
                key={api.id}
                logo={logoSrc}
                name={api.name}
                value={api.uuid}
                checked={selectedConnection === api.uuid}
                onChange={handleConnectionChange}
              />
            );
          })}
        </Connections>
        <TableNavbar> 
             <Button onClick={() => setStep(3)}>Open</Button>    
             <Button onClick={() => ApiService.deleteApiByUuid(selectedConnection)}>Delete</Button>    
             <Button onClick={() => setStep(1)}>New Connection Type</Button>
        </TableNavbar>
      </Card>
    )}
    {step === 1 && (
      <NewConnectionTypeModal_Step1 
        setModalIsOpen={handleCloseModal} 
        onBack={() => setStep(0)} 
        onNext={() => setStep(2)}
        formData={formData}
        onChange={handleChange}
        setFormData={setFormData}
        logoFile={logoFile}
      />
    )}
    {step === 2 && (
      <NewConnectionTypeModal_Step2 
        setModalIsOpen={handleCloseModal} 
        onBack={() => setStep(1)} 
        onFinish={handleFinish}
        formData={formData}
        onChange={handleChange}
        setFormData={setFormData}
        apiEndpoints={apiEndpoints}
        setApiEndpoints={setApiEndpoints}
      />
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
