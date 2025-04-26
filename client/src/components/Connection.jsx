
import styles from './Connection.module.css'
import PropTypes from 'prop-types'

function Connection({logo, name, value, checked, onChange}) {
  return (
    <div className={styles['connection']} onClick={() => onChange(value)}>
       <img src={logo} className={styles['connection__logo']} />
       <p 
         className={styles['connection__name']}
         style={{ color: checked ? '#449fff' : 'inherit' }}
       >
         {name}
       </p>
       <input 
         type="radio" 
         name="connectionType"
         style={{display: 'none'}}
         value={value}
         checked={checked}
         onChange={() => onChange(value)}
         onClick={(e) => e.stopPropagation()}
       />
    </div>
  )
}

export default Connection
Connection.propTypes = {
  logo: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  value: PropTypes.string.isRequired,
  checked: PropTypes.bool.isRequired,
  onChange: PropTypes.func.isRequired
}
