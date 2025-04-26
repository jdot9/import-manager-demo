
import PropTypes from 'prop-types'
import { useState, useEffect } from 'react'
import styles from './ui/Form.module.css'

function ApiForm({ fields = [], formData = {}, onChange, onSubmit }) {
  // State for file input hover effect
  const [fileInputHover, setFileInputHover] = useState(false);
  
  // Get the file field's selectedFileName from props
  const fileField = fields.find(f => f.type === 'file');
  const propsFileName = fileField?.selectedFileName || null;
  
  // State for tracking selected file name (for immediate display)
  const [selectedFileName, setSelectedFileName] = useState(propsFileName);

  // Sync selectedFileName when props change (navigate back case)
  useEffect(() => {
    if (propsFileName) {
      setSelectedFileName(propsFileName);
    }
  }, [propsFileName]);

  // Initialize dynamic rows for dual input fields from formData
  const [dynamicRows, setDynamicRows] = useState(() => {
    const initialRows = {};
    fields.forEach(field => {
      if (field.dualInput) {
        // Check if formData has saved data for this field
        if (formData[field.name]) {
          try {
            const savedData = typeof formData[field.name] === 'string' 
              ? JSON.parse(formData[field.name]) 
              : formData[field.name];
            
            // Convert object back to array of {key, value} pairs
            const rowsArray = Object.entries(savedData).map(([key, value]) => ({ key, value }));
            // Add an empty row at the end for new entries
            initialRows[field.name] = [...rowsArray, { key: '', value: '' }];
          } catch {
            // If parsing fails, start with empty row
            initialRows[field.name] = [{ key: '', value: '' }];
          }
        } else {
          initialRows[field.name] = [{ key: '', value: '' }];
        }
      }
    });
    return initialRows;
  });

  // Sync dynamicRows with formData when navigating back to a step or clearing form
  useEffect(() => {
    const updatedRows = {};
    fields.forEach(field => {
      if (field.dualInput) {
        if (formData[field.name] && formData[field.name].trim() !== '') {
          try {
            const savedData = typeof formData[field.name] === 'string' 
              ? JSON.parse(formData[field.name]) 
              : formData[field.name];
            
            // Convert object back to array of {key, value} pairs
            const rowsArray = Object.entries(savedData).map(([key, value]) => ({ key, value }));
            // Add an empty row at the end for new entries
            updatedRows[field.name] = [...rowsArray, { key: '', value: '' }];
          } catch {
            // Reset to empty row if parsing fails
            updatedRows[field.name] = [{ key: '', value: '' }];
          }
        } else {
          // Reset to empty row when formData is cleared
          updatedRows[field.name] = [{ key: '', value: '' }];
        }
      }
    });
    
    if (Object.keys(updatedRows).length > 0) {
      setDynamicRows(prev => ({ ...prev, ...updatedRows }));
    }
  }, [formData, fields]);

  const handleDualInputChange = (fieldName, index, type, value) => {
    setDynamicRows(prevRows => {
      const updatedRows = [...(prevRows[fieldName] || [{ key: '', value: '' }])];
      
      // Update the specific field
      updatedRows[index] = { ...updatedRows[index], [type]: value };

      // Check if we need to add a new row
      // Only if we're editing the last row and both fields are now filled
      const isLastRow = index === updatedRows.length - 1;
      
      if (isLastRow) {
        const currentRow = updatedRows[index];
        const keyFilled = currentRow.key && currentRow.key.trim() !== '';
        const valueFilled = currentRow.value && currentRow.value.trim() !== '';
        
        // Add a new empty row only if BOTH fields are filled
        if (keyFilled && valueFilled) {
          // Check if we haven't already added an empty row
          const hasEmptyRowAfter = updatedRows.length > index + 1;
          if (!hasEmptyRowAfter) {
            updatedRows.push({ key: '', value: '' });
          }
        }
      }

      return { ...prevRows, [fieldName]: updatedRows };
    });

    // Update formData with the combined data - use setTimeout to ensure state is updated
    setTimeout(() => {
      setDynamicRows(currentRows => {
        const filledRows = (currentRows[fieldName] || []).filter(row => row.key || row.value);
        // Convert array of {key, value} objects to plain object
        const dataObject = filledRows.reduce((acc, row) => {
          if (row.key) {
            acc[row.key] = row.value;
          }
          return acc;
        }, {});
        const syntheticEvent = {
          target: {
            name: fieldName,
            value: JSON.stringify(dataObject)
          }
        };
        onChange(syntheticEvent);
        return currentRows;
      });
    }, 0);
  };
  
  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form className={styles['form']} onSubmit={handleSubmit}>
      {fields.map((field, index) => {
        const inputType = field.type || 'text';
        const isSelectField = field.label.includes('Type') || field.label.includes('Method');
        const isTextarea = inputType === 'textarea';
        const isDualInput = field.dualInput;
        const isFileInput = inputType === 'file';

        return (
          <div key={field.name || index} className={styles['form-group']}>
            <label htmlFor={field.name} className={styles['form-group__label']}>
              {field.label}:
            </label>
            {isSelectField ? (
              <select id={field.name} 
                      name={field.name}
                      value={formData[field.name] || ''}
                      onChange={onChange} 
                      required={field.required}
                      className={styles['form-group__input']}
              >
                {field.options && field.options.map((option, idx) => (
                  <option key={idx} value={option.value || option}>
                    {option.label || option}
                  </option>
                ))}
              </select>
            ) : isTextarea ? (
              <textarea 
                id={field.name}
                name={field.name}
                value={formData[field.name] || ''}
                onChange={onChange}
                required={field.required}
                placeholder={field.placeholder}
                className={styles['form-group__input']}
                rows={field.rows || 4}
              />
            ) : isFileInput ? (
              <div 
                style={{ position: 'relative', display: 'flex', alignItems: 'center' }}
                onMouseEnter={() => setFileInputHover(true)}
                onMouseLeave={() => setFileInputHover(false)}
              >
                <input 
                  id={field.name}
                  name={field.name}
                  type="file"
                  onChange={(e) => {
                    // Update local state with selected filename
                    if (e.target.files && e.target.files[0]) {
                      setSelectedFileName(e.target.files[0].name);
                    }
                    // Call parent onChange
                    onChange(e);
                  }}
                  accept={field.accept || '.jpg, .jpeg, .png'}
                  style={{ 
                    position: 'absolute', 
                    opacity: 0, 
                    width: '100%', 
                    height: '100%', 
                    cursor: 'pointer',
                    zIndex: 1
                  }}
                />
                <div 
                  className={styles['form-group__input']}
                  style={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'space-between',
                    color: selectedFileName ? 'inherit' : '#999',
                    cursor: 'pointer',
                    borderColor: fileInputHover ? '#007bff' : undefined,
                    boxShadow: fileInputHover ? '0 0 15px rgba(0, 123, 255, 0.3)' : undefined,
                    transition: 'box-shadow 0.3s ease, border-color 0.3s ease'
                  }}
                >
                  <span>{selectedFileName || 'Choose a file...'}</span>
                  <span style={{ fontSize: '0.85em', color: '#666' }}>Browse</span>
                </div>
              </div>
            ) : isDualInput ? (
              <>
                {(dynamicRows[field.name] || [{ key: '', value: '' }]).map((row, idx) => (
                  <div key={idx} className={styles['form-group__dual-input']} style={{ marginBottom: idx < (dynamicRows[field.name]?.length - 1 || 0) ? '10px' : '0' }}>
                    <input 
                      id={`${field.name}_key_${idx}`}
                      name={`${field.name}_key_${idx}`}
                      type="text"
                      value={row.key}
                      onChange={(e) => handleDualInputChange(field.name, idx, 'key', e.target.value)}
                      required={field.required && idx === 0}
                      placeholder={field.placeholder1 || 'Key'}
                    />
                    <input 
                      id={`${field.name}_value_${idx}`}
                      name={`${field.name}_value_${idx}`}
                      type="text"
                      value={row.value}
                      onChange={(e) => handleDualInputChange(field.name, idx, 'value', e.target.value)}
                      required={field.required && idx === 0}
                      placeholder={field.placeholder2 || 'Value'}
                    />
                  </div>
                ))}
              </>
            ) : (
              <input 
                id={field.name}
                name={field.name}
                type={inputType}
                value={formData[field.name] || ''}
                onChange={onChange}
                required={field.required}
                placeholder={field.placeholder}
                className={styles['form-group__input']}
              />
              
            )}
          </div>
        );
      })}

    </form>
  )
}

ApiForm.propTypes = {
  fields: PropTypes.arrayOf(PropTypes.shape({
    name: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    type: PropTypes.string,
    required: PropTypes.bool,
    rows: PropTypes.number,
    dualInput: PropTypes.bool,
    placeholder1: PropTypes.string,
    placeholder2: PropTypes.string,
    options: PropTypes.arrayOf(PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.shape({
        label: PropTypes.string,
        value: PropTypes.string
      })
    ]))
  })).isRequired,
  formData: PropTypes.object.isRequired,
  onChange: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired
}

export default ApiForm
