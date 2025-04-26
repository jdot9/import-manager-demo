import React, { useEffect, useState } from 'react'
import Card from '../ui/Card'
import TableNavbar from '../ui/TableNavbar'
import Button from '../ui/Button'
import ApiService from '../../services/ApiService'
import PropTypes from 'prop-types'
import formStyles from '../ui/Form.module.css'
import ConnectionService from '../../services/ConnectionService'

function SelectedConnectionModal({onBack, onCreate, selectedConnection, userUuid}) {

  const [api, setApi] = useState(null);
  const [apiEndpoints, setApiEndpoints] = useState([]);
  // useEffect send GET request to retrieve API by its id
  useEffect(() => {
    ApiService.getApiByUuid(selectedConnection).then(data => {
      console.log('API retrieved:', data);
      setApi(data);
    });
    ApiService.getApiEndpointsByApiId(api?.id).then(data => {
      console.log('API endpoints retrieved:', data);
      setApiEndpoints(data);
    });
  }, [selectedConnection, api?.id]);

  const logoSrc = api?.logoUrl 
    ? `http://localhost:8080/api/logos/${api.logoUrl.replace('Logos/', '')}`
    : null;
  console.log('API endpoints:', apiEndpoints);

  const [formData, setFormData] = useState({
    name: "",
    description: "",
    userUuid: userUuid
  });

  function handleChange(e) {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  }

  function handleSubmit(e) {
    e.preventDefault(); // prevent page refresh
    console.log(formData); // form data here
  }
  return (
    <Card title={api?.name} logo={logoSrc}>
    
    <form className={formStyles['form']} action="">
        <div className={formStyles['form-group']}>
            <label htmlFor='name' className={formStyles['form-group__label']}>Name</label>
            <input type='text' id='name' name='name' onChange={handleChange} className={formStyles['form-group__input']} />
        </div>
        <div className={formStyles['form-group']}>
            <label htmlFor='description' className={formStyles['form-group__label']}>Description</label>
            <input type='text' id='description' name='description' onChange={handleChange} className={formStyles['form-group__input']} />
        </div>
        <div className={formStyles['form-group']}>
            <label htmlFor='apiEndpoint' className={formStyles['form-group__label']}>API Endpoint</label>
            <select name="" id="" className={formStyles['form-group__select']}>
                {apiEndpoints.map((endpoint) => (
                    <option key={endpoint.id} value={endpoint.id}>{endpoint.name} ({endpoint.path})</option>
                ))}
         
            </select>
        </div>
    </form>

    <TableNavbar>
      <Button onClick={onBack}>Back</Button>
      <Button onClick={() => ConnectionService.saveConnection(formData)}>Create</Button>
    </TableNavbar>
  </Card>
  );
}

SelectedConnectionModal.propTypes = {
  onBack: PropTypes.func.isRequired,
  onCreate: PropTypes.func.isRequired,
  selectedConnection: PropTypes.string.isRequired
};

export default SelectedConnectionModal;
