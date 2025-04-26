package com.dotwavesoftware.importscheduler.features.User.repository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;

import com.dotwavesoftware.importscheduler.features.User.entity.UserRoleEntity;
import com.dotwavesoftware.importscheduler.shared.entity.BaseRepository;
import com.dotwavesoftware.importscheduler.shared.util.ConversionUtil;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class UserRoleRepository extends BaseRepository<UserRoleEntity> {
    
    private static final Logger logger = Logger.getLogger(UserRoleRepository.class.getName());

    public UserRoleRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public int save(UserRoleEntity userRole) {
        String sql = "INSERT INTO user_roles (uuid, role, created_at) VALUES (?, ?, NOW())";
        
        int rowsAffected = jdbcTemplate.update(sql,
            ConversionUtil.uuidToBytes(userRole.getUuid()),
            userRole.getRole()
        );
        
        logger.info("Saving user role to database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public List<UserRoleEntity> findAll() {
        String sql = "SELECT * FROM user_roles";
        List<UserRoleEntity> userRoles = jdbcTemplate.query(sql, new UserRoleRowMapper());
        logger.info("Retrieving all user roles from database. Found: " + userRoles.size());
        return userRoles;
    }

    @Override
    public Optional<UserRoleEntity> findById(Integer id) {
        String sql = "SELECT * FROM user_roles WHERE id = ?";
        try {
            logger.info("Retrieving user role with id " + id + " from database.");
            UserRoleEntity userRole = jdbcTemplate.queryForObject(sql, new UserRoleRowMapper(), id);
            return Optional.ofNullable(userRole);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No user role found with id " + id);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve user role with id " + id + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public int update(UserRoleEntity userRole, Integer id) {
        String sql = "UPDATE user_roles SET role = ?, modified_at = NOW() WHERE id = ?";
        
        int rowsAffected = jdbcTemplate.update(sql,
            userRole.getRole(),
            id
        );
        
        logger.info("Updating user role with id " + id + " in database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }

    @Override
    public int deleteById(Integer id) {
        String sql = "DELETE FROM user_roles WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        logger.info("Deleting user role with id " + id + " from database. Rows affected: " + rowsAffected);
        return rowsAffected;
    }
    
    public Optional<UserRoleEntity> findByRole(String roleName) {
        String sql = "SELECT * FROM user_roles WHERE role = ?";
        try {
            logger.info("Retrieving user role with name: " + roleName);
            UserRoleEntity userRole = jdbcTemplate.queryForObject(sql, new UserRoleRowMapper(), roleName);
            return Optional.ofNullable(userRole);
        } catch (EmptyResultDataAccessException ex) {
            logger.warning("No user role found with name: " + roleName);
            return Optional.empty();
        } catch (Exception e) {
            logger.warning("Failed to retrieve user role with name " + roleName + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}
