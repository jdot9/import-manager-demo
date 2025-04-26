
import styles from './Connections.module.css'
import PropTypes from 'prop-types'

function Connections({children}) {

    const zeroConnectionsMessage = (
        <div style={{display: 'block'}}>
          <h2 style={{textAlign: 'left', color: 'red'}}>No connection types found.</h2>
          <p>Click Custom Integration to add a new connection type.</p>
        </div>
      );

  return (
    <div className={styles['connections']}>
       {children || zeroConnectionsMessage}
    </div>
  )
}

export default Connections
Connections.propTypes = {
  children: PropTypes.node
}