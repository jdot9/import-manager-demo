import React, { useState } from 'react'
import Button from '../ui/Button';
import Card from '../ui/Card';
import Form from '../ui/Form';
import ImportScheduleForm from '../ImportScheduleForm';
import PropTypes from 'prop-types';

// Step 6: Schedule the Import.
function NewImportModal_Step6({modalIsOpen, setModalIsOpen, hubspotConnectionId, hubspotListId, five9ConnectionId, five9DialingListId, onBack, onSave}) {

    const [formData, setFormData] = useState({
        importName: "",
        startDate: "",
        stopDate: "",
        recurring: false,
        daily: false,
        monthly: false,
        yearly: false,
        indefinetely: false,
        immediately: false,
        emailNotifications: false,
        email: "",
        sunday: false,
        monday: false,
        tuesday: false,
        wednesday: false,
        thursday: false,
        friday: false,
        saturday: false,
    });

    const fields = [
        { name: 'importName', label: 'Import Name', required: true },
        { name: 'startDate', label: 'Start Date/Time', type: 'datetime-local', required: true },
        { name: 'stopDate', label: 'Stop Date/Time', type: 'datetime-local', required: true },
        { name: 'email', label: 'Email Address', type: 'email', required: true }

    ];

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormData({ ...formData, [name]: value });
    };

  return (
    <Card title="Schedule Import" closeModal={() => setModalIsOpen(false)}>
      <ImportScheduleForm previousStepBtn={onBack} fields={fields} formData={formData} 
            onChange={handleChange} setFormData={setFormData} onSubmit={onSave} />
    </Card>
  )
}

NewImportModal_Step6.propTypes = {
  setModalIsOpen: PropTypes.func.isRequired,
  onBack: PropTypes.func.isRequired,
  onSave: PropTypes.func.isRequired,
  fields: PropTypes.arrayOf(PropTypes.shape({
    name: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    type: PropTypes.string,
    required: PropTypes.bool
  })),
  formData: PropTypes.object.isRequired,
  onChange: PropTypes.func.isRequired,
}

export default NewImportModal_Step6

