import Card from "../components/ui/Card"
import Button from "../components/ui/Button"
import { NavLink } from "react-router"
import styles from "./LoginPage.module.css"



function RegistrationPage() {
  return (
    <Card title={"Registration"}>
      <form action="">
        <div className="form-group">
            <label htmlFor="firstName" className="form-group__label">First Name</label>
            <input type="text" className="form-group__input" />
        </div>
 
        <div className="form-group">
              <label htmlFor="lastName" className="form-group__label">Last Name</label>
              <input type="text" className="form-group__input"/>
        </div>

        <div className="form-group">
            <label htmlFor="email" className="form-group__label">Email</label>
            <input type="email" className="form-group__input" />
        </div>
 
        <div className="form-group">
              <label htmlFor="newPassword" className="form-group__label">New Password</label>
              <input type="password" className="form-group__input"/>
        </div>

        <div className="form-group">
              <label htmlFor="confirmPassword" className="form-group__label">Confirm Password</label>
              <input type="password" className="form-group__input"/>
        </div>


        <div className="form-group">
              <label htmlFor="question" className="form-group__label">Secret Question</label>
              <input placeholder="Ask a question only you know the answer to." type="text" className="form-group__input"/>
        </div>

        <div className="form-group">
              <label htmlFor="answer" className="form-group__label">Secret Answer</label>
              <input type="password" className="form-group__input"/>
        </div>

        <div className="form-group">
            <Button style={{width: "100%", fontSize: "larger", fontWeight: '900'}}>Register</Button>
        </div>
            
            <NavLink to="/login" className={styles.link}>Go back</NavLink>

      </form>
    </Card>
  )
}

export default RegistrationPage
