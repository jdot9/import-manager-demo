
import PropTypes from 'prop-types'
import { useState, useEffect, useRef } from 'react'
import Card from '../ui/Card'
import TableNavbar from '../ui/TableNavbar'
import Button from '../ui/Button'
import ApiForm from '../ApiForm'
import ApiService from '../../services/ApiService'


function NewConnectionTypeModal_Step1({setModalIsOpen, onBack, onNext, formData, onChange, setFormData, logoFile}) {
    const [apiTypes, setApiTypes] = useState([]);
    const [authTypes, setAuthTypes] = useState([]);
    const [loading, setLoading] = useState(true);
    const defaultsSet = useRef(false);

    useEffect(() => {
        const fetchTypes = async () => {
            try {
                const [apiTypesData, authTypesData] = await Promise.all([
                    ApiService.getAllApiTypes(),
                    ApiService.getAllApiAuthTypes()
                ]);
                
                // Map to objects with value (ID as string) and label (type name)
                const apiTypesArray = apiTypesData.map(type => ({
                    value: String(type.id),
                    label: type.type
                }));
                const authTypesArray = authTypesData.map(type => ({
                    value: String(type.id),
                    label: type.type
                }));
                
                setApiTypes(apiTypesArray);
                setAuthTypes(authTypesArray);
                
                // Set default values only once if not already set
                if (!defaultsSet.current && apiTypesArray.length > 0 && !formData.apiType) {
                    setFormData(prev => ({ ...prev, apiType: apiTypesArray[0].value }));
                }
                if (!defaultsSet.current && authTypesArray.length > 0 && !formData.authType) {
                    setFormData(prev => ({ ...prev, authType: authTypesArray[0].value }));
                }
                defaultsSet.current = true;
                
                setLoading(false);
            } catch (error) {
                console.error('Error fetching types:', error);
                setLoading(false);
            }
        };

        fetchTypes();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);
    const handleBack = () => {
        onBack();
    };

    const handleNext = () => {
        onNext();
    };

    const api_fields = [
        { name: 'apiName', label: 'API Name', placeholder: 'Google Maps', type: 'text', required: true },
        { name: 'baseUrl', label: 'Base URL', placeholder: 'https://maps.googleapis.com/maps/api', type: 'url', required: true },
        { name: 'logoUrl', label: 'Logo', type: 'file', accept: '.jpg, .jpeg, .png', required: false, selectedFileName: logoFile?.name },
        { 
            name: 'apiType', 
            label: 'API Type', 
            required: true,
            options: apiTypes
        },
        { 
            name: 'authType', 
            label: 'Authentication Type', 
            required: true,
            options: authTypes
        },
        { name: 'authDetails', label: 'Authentication Details', dualInput: true, placeholder1: 'Key', placeholder2: 'Value', required: true },
    ];

    const saveApi = () => {
        // Only Step 1 fields (API data)
        const apiData = {
            apiName: formData.apiName,
            baseUrl: formData.baseUrl,
            logoUrl: formData.logoUrl,
            apiType: formData.apiType,
            authType: formData.authType,
            authDetails: formData.authDetails
        };
        setFormData(apiData);
        console.log('API Data:', apiData);
    }

  if (loading) {
    return (
      <Card title="Define API" closeModal={setModalIsOpen}>
        <p>Loading...</p>
      </Card>
    );
  }

  return (
    <Card title="Define API" closeModal={setModalIsOpen}>
      <ApiForm fields={api_fields} formData={formData} onChange={onChange} onSubmit={saveApi} />
      <TableNavbar>
        <Button onClick={handleBack}>Back</Button>
        <Button onClick={handleNext}>Next</Button>
      </TableNavbar>
    </Card>
  );
}

NewConnectionTypeModal_Step1.propTypes = {
  setModalIsOpen: PropTypes.func.isRequired,
  onBack: PropTypes.func.isRequired,
  onNext: PropTypes.func.isRequired,
  formData: PropTypes.object.isRequired,
  onChange: PropTypes.func.isRequired,
  setFormData: PropTypes.func.isRequired,
  logoFile: PropTypes.object
};

export default NewConnectionTypeModal_Step1;

