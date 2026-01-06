import { useState } from 'react'
import Card from '../ui/Card'
import TableNavbar from '../ui/TableNavbar'
import Button from '../ui/Button'
import PropTypes from 'prop-types'
import formStyles from '../ui/Form.module.css'
import ConnectionService from '../../services/ConnectionService'
import hubspotlogo from '../../assets/Hubspot-Logo.jpg'
import five9logo from '../../assets/Five9-Logo.jpg'

function SelectedConnectionModal({onBack, onClose, onConnectionSaved, selectedConnection, userUuid}) {

  const [formData, setFormData] = useState({
    name: "",
    description: "",
    hubspotAccessToken: "",
    five9Username: "",
    five9Password: "",
    userUuid: userUuid
  });

  console.log('User UUID:', userUuid);

  const handleCreate = async () => {
    const connectionData = {
      ...formData,
      userUuid: userUuid  // Always use the prop value
    };
    console.log('Creating connection with data:', connectionData);
    const success = await ConnectionService.saveConnection(connectionData);
    
    if (success) {
      // Refresh the connections list and close the modal
      if (onConnectionSaved) {
        onConnectionSaved();
      }
      if (onClose) {
        onClose();
      }
    }
  };

  function handleChange(e) {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  }

  const selectedConnectionLogo = selectedConnection === "hubspot" ? hubspotlogo : five9logo;

  // Validate required fields based on connection type
  const isFormValid = () => {
    if (!formData.name.trim()) return false;
    
    if (selectedConnection === "hubspot") {
      return formData.hubspotAccessToken.trim();
    } else {
      return formData.five9Username.trim() && formData.five9Password.trim();
    }
  };

  return (
    <Card logo={selectedConnectionLogo} closeModal={onClose}>
    
    <form className={formStyles['form']} action="">
        <div className={formStyles['form-group']}>
            <label htmlFor='name' className={formStyles['form-group__label']}>Name</label>
            <input type='text' id='name' name='name' value={formData.name} onChange={handleChange} className={formStyles['form-group__input']} />
        </div>

        <div className={formStyles['form-group']}>
            <label htmlFor='description' className={formStyles['form-group__label']}>Description</label>
            <textarea id='description' name='description' value={formData.description} onChange={handleChange} className={formStyles['form-group__input']} />
        </div>

        {selectedConnection === "hubspot" ? (
          <>
            <div className={formStyles['form-group']}>
              <label htmlFor='hubspotAccessToken' className={formStyles['form-group__label']}>Access Token:</label>
              <input type='password' id='hubspotAccessToken' name='hubspotAccessToken' value={formData.hubspotAccessToken} onChange={handleChange} className={formStyles['form-group__input']} />
            </div>
          </>
        ) : (
          <>
            <div className={formStyles['form-group']}>
              <label htmlFor='five9Username' className={formStyles['form-group__label']}>Username:</label>
              <input type='text' id='five9Username' name='five9Username' value={formData.five9Username} onChange={handleChange} className={formStyles['form-group__input']} />
            </div>
            <div className={formStyles['form-group']}>
              <label htmlFor='five9Password' className={formStyles['form-group__label']}>Password:</label>
              <input type='password' id='five9Password' name='five9Password' value={formData.five9Password} onChange={handleChange} className={formStyles['form-group__input']} />
            </div>
          </>
        )}

    </form>

    <TableNavbar>
      <Button onClick={onBack}>Back</Button>
      <Button 
        onClick={handleCreate} 
        disabled={!isFormValid()}
      >
        Create
      </Button>
    </TableNavbar>
  </Card>
  );
}

SelectedConnectionModal.propTypes = {
  onBack: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
  onConnectionSaved: PropTypes.func,
  selectedConnection: PropTypes.string.isRequired,
  userUuid: PropTypes.string.isRequired
};

export default SelectedConnectionModal;
