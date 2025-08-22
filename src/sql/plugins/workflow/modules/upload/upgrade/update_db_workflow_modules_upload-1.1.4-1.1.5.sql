-- liquibase formatted sql
-- changeset workflow-upload:update_db_workflow_modules_upload-1.1.4-1.1.5.sql
-- preconditions onFail:MARK_RAN onError:WARN


alter table workflow_task_upload_files MODIFY COLUMN id_upload_file int auto_increment NOT NULL;