
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
        title  VARCHAR(200) DEFAULT '' NULL,
        is_mandatory SMALLINT DEFAULT 0,
	PRIMARY KEY (id_task)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ---------------------------------------------------------
-- Table structure for table workflow_task_upload_history --
-- ---------------------------------------------------------
CREATE TABLE workflow_task_upload_files
(
	id_upload_file INT DEFAULT 0 NOT NULL,
	id_history INT DEFAULT 0 NOT NULL,
	id_file  INT DEFAULT 0 NOT NULL,
	PRIMARY KEY (id_upload_file)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ---------------------------------------------------------
-- Table structure for table workflow_task_upload_history --
-- ---------------------------------------------------------
CREATE TABLE workflow_task_upload_history
(
	id_history INT DEFAULT 0 NOT NULL,
	id_task INT DEFAULT 0 NOT NULL,	
	PRIMARY KEY (id_history, id_task)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE INDEX task_upload_history_id_history_fk ON workflow_task_upload_history(id_history);
CREATE INDEX task_upload_history_id_task_fk ON workflow_task_upload_history(id_task);


ALTER TABLE workflow_task_upload_history ADD CONSTRAINT fk_task_upload_history_id_history FOREIGN KEY (id_history)
	REFERENCES workflow_resource_history(id_history) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE workflow_task_upload_history ADD CONSTRAINT fk_task_upload_history_id_task FOREIGN KEY (id_task)
	REFERENCES workflow_task(id_task) ON DELETE RESTRICT ON UPDATE RESTRICT;


CREATE INDEX task_upload_files_id_file_fk ON workflow_task_upload_files(id_file);
CREATE INDEX task_upload_files_id_history_fk ON workflow_task_upload_history(id_history);



ALTER TABLE workflow_task_upload_files ADD CONSTRAINT fk_task_upload_files_id_history FOREIGN KEY (id_history)
	REFERENCES workflow_resource_history(id_history) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE workflow_task_upload_files ADD CONSTRAINT fk_task_upload_files_id_file FOREIGN KEY (id_file)
	REFERENCES core_file(id_file) ON DELETE RESTRICT ON UPDATE RESTRICT;