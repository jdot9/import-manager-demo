import React from 'react'
import Button from '../ui/Button';
import TableNavbar from '../ui/TableNavbar';
import Form from '../ui/Form';
import styles from '../ui/Form.module.css';
import Dropdown from '../ui/Dropdown';

// Step 5: Map HubSpot Properties to Five9 Contact Fields.
function NewImportModal_Step5({modalIsOpen, setModalIsOpen, hubspotConnectionId, hubspotListId, five9ConnectionId, five9DialingListId, onBack, onComplete}) {

  return (
    <div style={{backgroundColor: '#2d3e50'}}>
      <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
          <h1 style={{color: 'white', margin: 0}}>{"Map HubSpot Properties to Five9 Contact Fields"}</h1>
          <h1 style={{color: 'white', margin: 0, cursor: 'pointer'}} onClick={() => setModalIsOpen(false)}>X</h1>
      </div>
      <div className="table-container">
        <table style={{marginTop: "1%", backgroundColor: 'white'}}>
          <thead>
              <tr>
                  <th style={{position: 'sticky', top: 0, borderTop: 'none'}}><input type="checkbox" /></th>
                  <th style={{position: 'sticky', top: 0, borderTop: 'none'}}>HubSpot Property Name</th>
                  <th style={{position: 'sticky', top: 0, borderTop: 'none'}}>Five9 Contact Field Name</th>
                  <th style={{position: 'sticky', top: 0, borderTop: 'none'}}>Format</th>
                  <th style={{position: 'sticky', top: 0, borderTop: 'none'}}>Five9 Key</th>

              </tr>
          </thead>
          <tbody>
              <tr style={{backgroundColor: 'white'}}>
                  <td><input type="checkbox" /></td>
                  <td>Cell Phone Number</td>
                  <td>number1</td>
                
                  <td></td>
                  <td><input type="checkbox" /></td>
              </tr>
              <tr style={{backgroundColor: 'rgb(245, 248, 250)'}}>
                  <td><input type="checkbox" /></td>
                  <td>Email</td>
                  <td>email</td>
                  <td></td>
                  <td><input type="checkbox" /></td>

              </tr>
              <tr style={{backgroundColor: 'white'}}>
                  <td><input type="checkbox" /></td>
                  <td>First Name</td>
                  <td>first_name</td>
                  <td></td>
                  <td><input type="checkbox" /></td>
                
              </tr>
              <tr style={{backgroundColor: 'rgb(245, 248, 250)'}}>
                  <td><input type="checkbox" /></td>
                  <td>Last Name</td>
                  <td>last_name</td>
                  <td></td>
                  <td><input type="checkbox" /></td>
            
              </tr>
              <tr style={{backgroundColor: 'white'}}>
                  <td><input type="checkbox" /></td>
                  <td>Cell Phone Number</td>
                  <td>number1</td>
                  <td></td>
                  <td><input type="checkbox" /></td>
              </tr>
              <tr style={{backgroundColor: 'rgb(245, 248, 250)'}}>
                  <td><input type="checkbox" /></td>
                  <td>Email</td>
                  <td>email</td>
                  <td></td>
                  <td><input type="checkbox" /></td>
                
              </tr>
              <tr style={{backgroundColor: 'white'}}>
                  <td><input type="checkbox" /></td>
                  <td>First Name</td>
                  <td>first_name</td>
                  <td></td>
                  <td><input type="checkbox" /></td>
              </tr>
              <tr style={{backgroundColor: 'rgb(245, 248, 250)'}}>
                  <td><input type="checkbox" /></td>
                  <td>Last Name</td>
                  <td>last_name</td>
                  <td></td>
                  <td><input type="checkbox" /></td>
              </tr>
              <tr style={{backgroundColor: 'white'}}>
                  <td><input type="checkbox" /></td>
                  <td>Cell Phone Number</td>
                  <td>number1</td>
                  <td></td>
                  <td><input type="checkbox" /></td>
                  
              </tr>
              <tr style={{backgroundColor: 'rgb(245, 248, 250)'}}>
                  <td><input type="checkbox" /></td>
                  <td>Email</td>
                  <td>email</td>
                  <td></td>
                  <td><input type="checkbox" /></td>
              </tr>
              <tr style={{backgroundColor: 'white'}}>
                  <td><input type="checkbox" /></td>
                  <td>First Name</td>
                  <td>first_name</td>
                  <td></td>
                  <td><input type="checkbox" /></td>
              </tr>
              <tr style={{backgroundColor: 'rgb(245, 248, 250)'}}>
                  <td><input type="checkbox" /></td>
                  <td>Last Name</td>
                  <td>last_name</td>
                  <td></td>
                  <td><input type="checkbox" /></td>
              </tr>

          </tbody>
        </table>
      </div>

      <TableNavbar style={{display: 'flex', justifyContent: 'space-evenly'}}>
        <label className="form-group__label" style={{fontSize: '1.2rem', fontWeight: '900'}}>New HubSpot Property:</label>
        <input type="text" placeholder="Home Phone Number" className={styles['form-group__input']} style={{width: '15%', borderRadius: '15px', fontSize: '1.2rem', fontWeight: '900', height: '10px', marginTop: '2px'}}/>
        <label className="form-group__label" style={{fontSize: '1.2rem', fontWeight: '900'}}>New Five9 Contact Field:</label>
        <input type="text" placeholder="number2" className={styles['form-group__input']} style={{width: '15%', borderRadius: '15px', fontSize: '1.2rem', fontWeight: '900', height: '10px', marginTop: '2px'}}/>
        <label className="form-group__label" style={{fontSize: '1.2rem', fontWeight: '900'}}>New Format:</label>
        <input type="text" placeholder="number2" className={styles['form-group__input']} style={{width: '15%', borderRadius: '15px', fontSize: '1.2rem', fontWeight: '900', height: '10px', marginTop: '2px'}}/>
  
      </TableNavbar>

      <TableNavbar>
        
         <Button onClick={() => {}}>Add Mapping Field</Button>
         <Button onClick={() => {}}>Remove Mapping Field</Button>
         <Button onClick={() => {}}>Add New Format</Button>

         
         <Dropdown btnText="Formats" items={['(###)###-####', '##########']} style={{width: '21.7%'}}/> 
      
        
        <Button onClick={onBack}>Back</Button>
        <Button onClick={onComplete}>Next</Button>
      </TableNavbar>

    </div>
  );
}

export default NewImportModal_Step5

