
import Card from '../ui/Card'
import PropTypes from 'prop-types'
import { useState, useEffect, useRef } from 'react'
import TableNavbar from '../ui/TableNavbar'
import Button from '../ui/Button'
import ApiForm from '../ApiForm'
import ApiService from '../../services/ApiService'

function NewConnectionTypeModal_Step2({setModalIsOpen, onBack, onFinish, formData, onChange, setFormData, apiEndpoints, setApiEndpoints}) {
    const [httpMethods, setHttpMethods] = useState([]);
    const [apiTypes, setApiTypes] = useState([]);
    const [loading, setLoading] = useState(true);
    const defaultsSet = useRef(false);

    // Place in useApiTypes custom hook
    useEffect(() => {
        const fetchData = async () => {
            try {
                const [methodsData, apiTypesData] = await Promise.all([
                    ApiService.getAllApiRestMethods(),
                    ApiService.getAllApiTypes()
                ]);
                
                // Map to objects with value (ID as string) and label (method name)
                setHttpMethods(methodsData.map(method => ({
                    value: String(method.id),
                    label: method.method
                })));
                
                // Store API types for filtering logic
                setApiTypes(apiTypesData);
                setLoading(false);
            } catch (error) {
                console.error('Error fetching data:', error);
                setLoading(false);
            }
        };

        fetchData();
    }, []);
    
    const handleBack = () => {
        console.log('Step 2 - Current Form Data:', formData);
        onBack();
    };

    const integrate = () => {
        console.log('Step 2 - Integrating API with data:', formData);
        onFinish();
    };

    // Get the selected API Type from formData (which is now an ID as string)
    const selectedApiTypeId = formData?.apiType;
    const selectedApiTypeObj = apiTypes.find(type => String(type.id) === String(selectedApiTypeId));
    const selectedApiTypeName = selectedApiTypeObj?.type || '';
    const isRestApi = selectedApiTypeName === 'REST';
    const isSoapApi = selectedApiTypeName === 'SOAP';

    // Filter HTTP methods based on API Type - SOAP only uses POST
    const filteredHttpMethods = isSoapApi 
        ? httpMethods.filter(method => method.label === 'POST')
        : httpMethods;

    // Get the default HTTP method (DELETE, or first available for SOAP)
    const getDefaultHttpMethod = () => {
        if (filteredHttpMethods.length === 0) return '';
        const deleteMethod = filteredHttpMethods.find(method => method.label === 'DELETE');
        return deleteMethod ? deleteMethod.value : filteredHttpMethods[0].value;
    };

    // Set default HTTP method when filtered methods are available (default to DELETE)
    useEffect(() => {
        if (!defaultsSet.current && filteredHttpMethods.length > 0 && !formData.httpMethod) {
            setFormData(prev => ({ ...prev, httpMethod: getDefaultHttpMethod() }));
            defaultsSet.current = true;
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [filteredHttpMethods]);

    // Define all possible fields
    const allFields = [
        { name: 'name', label: 'API Endpoint Name', placeholder: 'Find Place', type: 'text', required: true },
        { name: 'path', label: 'API Endpoint Path', placeholder: '/place/findplacefromtext/json',type: 'text', required: true },
        { name: 'description', label: 'Description', placeholder: 'This endpoint is used to find a location.', type: 'text', required: false },
        { 
            name: 'httpMethod', 
            label: 'HTTP Method', 
            required: true,
            options: filteredHttpMethods
        },
        { name: 'headers', label: 'Headers', dualInput: true, placeholder1: 'Key', placeholder2: 'Value', required: false },
        { name: 'queryParameters', label: 'Query Parameters', dualInput: true, placeholder1: 'Key', placeholder2: 'Value', required: false },
        { name: 'soapAction', label: 'SOAP Action', placeholder: 'SOAPAction: "DoAuthorization"', type: 'text', required: false },
        { 
            name: isSoapApi ? 'soapEnvelope' : 'requestBody', 
            label: isSoapApi ? 'SOAP Envelope' : 'Request Body', 
            placeholder: isSoapApi 
                ? `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
  <soapenv:Header>
    ...
  </soapenv:Header>
  <soapenv:Body>
    ...
  </soapenv:Body>
</soapenv:Envelope>` 
                : `{
  "first_name": "Rick",
  "last_name": "Grimes",
  "email": "carl@twd.com"
}`, 
            type: 'textarea', 
            required: false,
            rows: isSoapApi ? 9 : 5
        }
  
    ];

    // Filter fields based on API Type - hide SOAP-specific fields for REST APIs
    const endpoint_fields = isRestApi 
        ? allFields.filter(field => field.name !== 'soapAction' && field.name !== 'soapEnvelope')
        : allFields;

    const saveEndpoint = () => {
        // Validate required fields
        if (!formData.name || formData.name.trim() === '') {
            alert('Please enter an endpoint name.');
            return;
        }
        if (!formData.path || formData.path.trim() === '') {
            alert('Please enter an endpoint path.');
            return;
        }
        if (!formData.httpMethod || formData.httpMethod.trim() === '') {
            alert('Please select an HTTP method.');
            return;
        }
        
        // Only Step 2 fields (Endpoint data)
        const endpointData = {
            name: formData.name,
            path: formData.path,
            httpMethod: formData.httpMethod,
            headers: formData.headers,
            queryParameters: formData.queryParameters,
            requestBody: formData.requestBody,
            soapEnvelope: formData.soapEnvelope,
            soapAction: formData.soapAction,
            description: formData.description
        };
        console.log('Endpoint Data:', endpointData);
        setApiEndpoints(prev => {
            const newArray = [...prev, endpointData];
            console.log('API Endpoints Array:', newArray);
            return newArray;
        });
        
        // Clear Step 2 form fields for next endpoint (keep Step 1 data, reset HTTP method to default)
        setFormData(prev => ({
            ...prev,
            name: '',
            path: '',
            httpMethod: getDefaultHttpMethod(),
            headers: '',
            queryParameters: '',
            requestBody: '',
            soapEnvelope: '',
            soapAction: '',
            description: ''
        }));
    };

  if (loading) {
    return (
      <Card style={{overflowY: 'auto', maxHeight: '80vh'}} title="Define API Endpoint" closeModal={setModalIsOpen}>
        <p>Loading...</p>
      </Card>
    );
  }

  return (
    <Card style={{overflowY: 'auto', maxHeight: '80vh'}} title="Define API Endpoint" closeModal={setModalIsOpen}>
      <ApiForm fields={endpoint_fields} formData={formData} onChange={onChange} onSubmit={saveEndpoint} />
      <p style={{fontWeight: '900', fontSize: 'large'}}>API Endpoints saved: {apiEndpoints.length}</p>
      <TableNavbar>
       
        <Button onClick={handleBack}>Back</Button>
        <Button onClick={saveEndpoint}>Save</Button>
        <Button onClick={integrate}>Finish</Button>
      </TableNavbar>
    </Card>
  )
}

NewConnectionTypeModal_Step2.propTypes = {
  setModalIsOpen: PropTypes.func.isRequired,
  onBack: PropTypes.func.isRequired,
  formData: PropTypes.object.isRequired,
  onChange: PropTypes.func.isRequired,
  setFormData: PropTypes.func.isRequired,
  onFinish: PropTypes.func.isRequired,
  apiEndpoints: PropTypes.array.isRequired,
  setApiEndpoints: PropTypes.func.isRequired
}

export default NewConnectionTypeModal_Step2

