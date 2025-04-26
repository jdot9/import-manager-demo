DROP DATABASE IF EXISTS importmanagerdb;
CREATE DATABASE importmanagerdb;
USE importmanagerdb;

CREATE TABLE user_roles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  role VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  oauth_provider VARCHAR(255) UNIQUE,
  oauth_user_id VARCHAR(255) UNIQUE,
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  user_role_id INT DEFAULT 2,
  last_login_at DATETIME,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_role_id) REFERENCES user_roles(id)
  ON UPDATE CASCADE ON DELETE SET NULL,
      CHECK (
        (email IS NOT NULL AND password IS NOT NULL)
        OR (oauth_provider IS NOT NULL AND oauth_user_id IS NOT NULL)
    )
);

CREATE TABLE imports (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  status VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  email_notification TINYINT,
  email VARCHAR(255),
  user_id INT,
  FOREIGN KEY (user_id) REFERENCES users(id)
  ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE api_types (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  type VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE api_auth_types (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  type VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE apis (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  base_url VARCHAR(255),
  api_type_id INT,
  api_auth_type_id INT,
  auth_details JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  logo_url VARCHAR(255),
  user_id INT,
  user_uuid BINARY(16),
  FOREIGN KEY (api_type_id) REFERENCES api_types(id)
  ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (api_auth_type_id) REFERENCES api_auth_types(id)
  ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE connections (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  status VARCHAR(255),
  import_id INT,
  api_id INT,
  user_uuid BINARY(16),
  FOREIGN KEY (import_id) REFERENCES imports(id) ON UPDATE CASCADE ON DELETE SET NULL,
  FOREIGN KEY (api_id) REFERENCES apis(id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (user_uuid) REFERENCES users(uuid) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE import_schedules (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  start_datetime DATETIME,
  stop_datetime DATETIME,
  day INT,
  month INT,
  sunday TINYINT,
  monday TINYINT,
  tuesday TINYINT,
  wednesday TINYINT,
  thursday TINYINT,
  friday TINYINT,
  saturday TINYINT,
  recurring TINYINT,
  yearly TINYINT,
  import_id INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (import_id) REFERENCES imports(id)
  ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE mapping_formats (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  format VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE connection_import_mappings (
  connection_id INT,
  import_id INT,
  uuid BINARY(16) NOT NULL UNIQUE,
  field_name VARCHAR(255),
  mapping_format_id INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (connection_id, import_id),
  FOREIGN KEY (connection_id) REFERENCES connections(id) ON UPDATE CASCADE ON DELETE CASCADE ,
  FOREIGN KEY (import_id) REFERENCES imports(id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (mapping_format_id) REFERENCES mapping_formats(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE api_rest_methods (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  method VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE api_endpoints (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  api_id INT,
  name VARCHAR(255),
  path VARCHAR(255),
  api_rest_method_id INT,
  headers JSON,
  query_parameters JSON,
  request_body TEXT,
  soap_envelope TEXT,
  soap_action TEXT,
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (api_id) REFERENCES apis(id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (api_rest_method_id) REFERENCES api_rest_methods(id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE api_call_logs (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  api_endpoint_id INT,
  request TEXT,
  response TEXT,
  status_code INT,
  error TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (api_endpoint_id) REFERENCES api_endpoints(id)
);

CREATE TABLE user_security_questions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  user_id INT,
  question VARCHAR(255),
  answer VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO user_roles (uuid,role)
VALUES (UUID_TO_BIN(UUID()), 'ADMIN'), (UUID_TO_BIN(UUID()), 'USER'), (UUID_TO_BIN(UUID()), 'GUEST');

INSERT INTO users (uuid, user_role_id, first_name, last_name, email, password)
VALUES (UUID_TO_BIN(UUID()), 1, 'Jason', 'Dotson', 'djason77@gmail.com', 'walker'); 

INSERT INTO mapping_formats (uuid, format)
VALUES (UUID_TO_BIN(UUID()), '##########'),(UUID_TO_BIN(UUID()), '(###)###-####'),(UUID_TO_BIN(UUID()), '###-###-####');

INSERT INTO api_types (uuid, type)
VALUES (UUID_TO_BIN(UUID()), 'REST'), (UUID_TO_BIN(UUID()), 'SOAP');

INSERT INTO api_auth_types (uuid, type)
VALUES
(UUID_TO_BIN(UUID()), 'API Key'),
(UUID_TO_BIN(UUID()), 'Basic Auth'),
(UUID_TO_BIN(UUID()), 'OAuth 2.0'),
(UUID_TO_BIN(UUID()), 'Bearer Token'),
(UUID_TO_BIN(UUID()), 'JWT'),
(UUID_TO_BIN(UUID()), 'HMAC'),
(UUID_TO_BIN(UUID()), 'mTLS'),
(UUID_TO_BIN(UUID()), 'WS-Security UsernameToken'),
(UUID_TO_BIN(UUID()), 'WS-Security X.509 Cert'),
(UUID_TO_BIN(UUID()), 'WS-Security SAML Token'),
(UUID_TO_BIN(UUID()), 'WS-Policy/WS-Trust'),
(UUID_TO_BIN(UUID()), 'Digest Auth'),
(UUID_TO_BIN(UUID()), 'No Auth');

INSERT INTO api_rest_methods (uuid, method)
VALUES (UUID_TO_BIN(UUID()), 'GET'), (UUID_TO_BIN(UUID()), 'POST'), (UUID_TO_BIN(UUID()), 'PUT'),
       (UUID_TO_BIN(UUID()), 'PATCH'),(UUID_TO_BIN(UUID()), 'DELETE'), (UUID_TO_BIN(UUID()), 'HEAD');


select * from users;


