import PropTypes from 'prop-types'
import styles from './Dropdown.module.css'
import Button from './Button'

function Dropdown({items, onClick, style, btnText}) {
  return (
    <div className={styles.dropdown} style={style}>
      <Button>{btnText}</Button>
      <div className={`${styles['dropdown-content']}`}>
        {items.map((item, index) => (
          <p key={index} onClick={() => onClick(item)} 
             className={styles['dropdown-content__item']}>{item}</p>
        ))}
      </div>
    </div>
  )
}

Dropdown.propTypes = {
  items: PropTypes.array.isRequired,
  onClick: PropTypes.func.isRequired,
  style: PropTypes.object,
  btnText: PropTypes.string
}

export default Dropdown
