import PropTypes from 'prop-types'
import styles from './Button.module.css'

function Button({children, onClick, style, className}) {
  return (
    <button className={`${styles.btn} ${styles['btn--primary']} ${className || ''}`} onClick={onClick} style={style}>
        {children}
    </button>
  )
}

Button.propTypes = {
  children: PropTypes.node.isRequired,
  onClick: PropTypes.func.isRequired,
  style: PropTypes.object,
  className: PropTypes.string
}

export default Button
