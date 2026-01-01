DROP DATABASE IF EXISTS importmanagerdemodb;
CREATE DATABASE importmanagerdemodb;
USE importmanagerdemodb;

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  oauth_provider VARCHAR(255),
  oauth_user_id VARCHAR(255) UNIQUE,
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  last_login_at DATETIME,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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

CREATE TABLE connections (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uuid BINARY(16) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modified_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  status VARCHAR(255),
  import_id INT,
  user_uuid BINARY(16),
  FOREIGN KEY (import_id) REFERENCES imports(id) ON UPDATE CASCADE ON DELETE SET NULL,
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

INSERT INTO users (uuid, first_name, last_name, email, password)
VALUES (UUID_TO_BIN(UUID()), 'Jason', 'Dotson', 'dotsonjason05@gmail.com', 'walker'); 

INSERT INTO mapping_formats (uuid, format)
VALUES (UUID_TO_BIN(UUID()), '##########'),(UUID_TO_BIN(UUID()), '(###)###-####'),(UUID_TO_BIN(UUID()), '###-###-####');

select * from users;
