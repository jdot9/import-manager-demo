import React, { useEffect, useState } from 'react'
import Card from '../ui/Card'
import TableNavbar from '../ui/TableNavbar'
import Button from '../ui/Button'
import ApiService from '../../services/ApiService'
import PropTypes from 'prop-types'
import formStyles from '../ui/Form.module.css'
import ConnectionService from '../../services/ConnectionService'
import hubspotlogo from '../../assets/Hubspot-Logo.jpg'
import five9logo from '../../assets/Five9-Logo.jpg'

function SelectedConnectionModal({onBack, onCreate, selectedConnection, userUuid}) {

 



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

  const selectedConnectionLogo = selectedConnection === "hubspot" ? hubspotlogo : five9logo;

  return (
    <Card logo={selectedConnectionLogo}>
    
    <form className={formStyles['form']} action="">
        <div className={formStyles['form-group']}>
            <label htmlFor='name' className={formStyles['form-group__label']}>Name</label>
            <input type='text' id='name' name='name' onChange={handleChange} className={formStyles['form-group__input']} />
        </div>

        <div className={formStyles['form-group']}>
            <label htmlFor='description' className={formStyles['form-group__label']}>Description</label>
            <textarea type='text' id='description' name='description' onChange={handleChange} className={formStyles['form-group__input']} />
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
