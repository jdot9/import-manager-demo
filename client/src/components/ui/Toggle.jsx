import { useId } from 'react'
import './Toggle.css'

function Toggle() {
  const id = useId();

  return (
    <div className="toggle">
        <input type="checkbox" id={id} className="toggle__input"/>
        <label htmlFor={id} className="toggle__button"></label>
    </div>
  )
}

export default Toggle
