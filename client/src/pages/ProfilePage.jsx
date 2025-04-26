import { useState, useEffect } from 'react'
import Card from '../components/ui/Card'
import UserService from '../services/UserService'
import Button from '../components/ui/Button'
import styles from './ProfilePage.module.css'

function ProfilePage() {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [isEditing, setIsEditing] = useState(false)
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: ''
  })

  useEffect(() => {
    const currentUser = UserService.getCurrentUser()
    if (currentUser) {
      setUser(currentUser)
      setFormData({
        firstName: currentUser.firstName || '',
        lastName: currentUser.lastName || '',
        email: currentUser.email || ''
      })
    }
    setLoading(false)
  }, [])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleEdit = () => {
    setIsEditing(true)
    setError('')
  }

  const handleCancel = () => {
    setIsEditing(false)
    // Reset form data to current user data
    if (user) {
      setFormData({
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        email: user.email || ''
      })
    }
    setError('')
  }

  const handleSave = async () => {
    setError('')
    
    try {
      const updatedUser = await UserService.updateUser(user.uuid, formData)
      
      // Update local storage with new user data
      const currentUserData = UserService.getCurrentUser()
      const updatedUserData = {
        ...currentUserData,
        firstName: updatedUser.firstName,
        lastName: updatedUser.lastName,
        email: updatedUser.email
      }
      localStorage.setItem('user', JSON.stringify(updatedUserData))
      
      setUser(updatedUserData)
      setIsEditing(false)
      console.log('Profile updated successfully')
    } catch (err) {
      console.error('Failed to update profile:', err)
      setError(err.response?.data || 'Failed to update profile. Please try again.')
    }
  }

  if (loading) {
    return (
      <div className={styles.profileContainer}>
        <Card style={{ maxWidth: '600px', width: '100%' }} title="Profile">
          <div className={styles.profileContent}>
            <p>Loading...</p>
          </div>
        </Card>
      </div>
    )
  }

  if (!user) {
    return (
      <div className={styles.profileContainer}>
        <Card style={{ maxWidth: '600px', width: '100%' }} title="Profile">
          <div className={styles.profileContent}>
            <p>No user data found. Please log in.</p>
          </div>
        </Card>
      </div>
    )
  }

  return (
    <div className={styles.profileContainer}>
      <Card style={{ maxWidth: '600px', width: '100%' }} title="Profile">
        <div className={styles.profileContent}>
          {error && (
            <div className={styles.errorMessage}>
              {error}
            </div>
          )}

          <div className={styles.profileInfo}>
            <div className={styles.infoGroup}>
              <label className={styles.label}>First Name</label>
              {isEditing ? (
                <input
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  className={styles.input}
                />
              ) : (
                <p className={styles.value}>{user.firstName || 'Not set'}</p>
              )}
            </div>

            <div className={styles.infoGroup}>
              <label className={styles.label}>Last Name</label>
              {isEditing ? (
                <input
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  className={styles.input}
                />
              ) : (
                <p className={styles.value}>{user.lastName || 'Not set'}</p>
              )}
            </div>

            <div className={styles.infoGroup}>
              <label className={styles.label}>Email</label>
              {isEditing ? (
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  className={styles.input}
                />
              ) : (
                <p className={styles.value}>{user.email}</p>
              )}
            </div>

            <div className={styles.infoGroup}>
              <label className={styles.label}>Role</label>
              <p className={styles.value}>{user.userRole || 'No role assigned'}</p>
            </div>

            <div className={styles.infoGroup}>
              <label className={styles.label}>User ID</label>
              <p className={styles.valueSmall}>{user.uuid}</p>
            </div>
          </div>

          <div className={styles.buttonGroup}>
            {isEditing ? (
              <>
                <Button onClick={handleSave}>
                  Save Changes
                </Button>
                <Button onClick={handleCancel} className={styles.cancelButton}>
                  Cancel
                </Button>
              </>
            ) : (
              <Button onClick={handleEdit}>
                Edit Profile
              </Button>
            )}
          </div>
        </div>
      </Card>
    </div>
  )
}

export default ProfilePage

