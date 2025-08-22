-- liquibase formatted sql
-- changeset workflow-upload:create_db_workflow_modules_upload.sql
-- preconditions onFail:MARK_RAN onError:WARN

DROP TABLE IF EXISTS workflow_task_upload_config;
DROP TABLE IF EXISTS workflow_task_upload_history;
DROP TABLE IF EXISTS workflow_task_upload_files;
-- ----------------------------------------------------------
-- Table structure for table workflow_task_upload_config --
-- ----------------------------------------------------------
CREATE TABLE workflow_task_upload_config
(
	id_task INT DEFAULT 0 NOT NULL,
	max_file INT DEFAULT 0 NOT NULL, 	
	max_size_file INT DEFAULT 0 NOT NULL, 	
        title  VARCHAR(255) DEFAULT '' NULL,
        is_mandatory SMALLINT DEFAULT 0,
	PRIMARY KEY (id_task)
);

-- ---------------------------------------------------------
-- Table structure for table workflow_task_upload_history --
-- ---------------------------------------------------------
CREATE TABLE workflow_task_upload_files
(
	id_upload_file INT auto_increment NOT NULL,
	id_history INT DEFAULT 0 NOT NULL,
	id_file  INT DEFAULT 0 NOT NULL,
	PRIMARY KEY (id_upload_file)
);

-- ---------------------------------------------------------
-- Table structure for table workflow_task_upload_history --
-- ---------------------------------------------------------
CREATE TABLE workflow_task_upload_history
(
	id_history INT DEFAULT 0 NOT NULL,
	id_task INT DEFAULT 0 NOT NULL,	
	PRIMARY KEY (id_history, id_task)
);

CREATE INDEX task_upload_history_id_history_fk ON workflow_task_upload_history(id_history);
CREATE INDEX task_upload_history_id_task_fk ON workflow_task_upload_history(id_task);


CREATE INDEX task_upload_files_id_file_fk ON workflow_task_upload_files(id_file);
CREATE INDEX task_upload_files_id_history_fk ON workflow_task_upload_history(id_history);
