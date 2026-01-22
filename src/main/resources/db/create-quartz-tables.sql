-- SQL script to create Quartz tables with autopilot_ prefix if they don't exist
-- Run this script if you get errors about missing Quartz tables

-- Note: This script creates the tables directly. 
-- If you prefer to use Liquibase, you may need to clear the changeset from DATABASECHANGELOG first.

-- Check and create autopilot_QRTZ_LOCKS
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'autopilot_qrtz_locks') THEN
        CREATE TABLE autopilot_qrtz_locks (
            SCHED_NAME VARCHAR(120) NOT NULL,
            LOCK_NAME VARCHAR(40) NOT NULL,
            PRIMARY KEY (SCHED_NAME, LOCK_NAME)
        );
    END IF;
END $$;

-- Check and create autopilot_QRTZ_JOB_DETAILS (this is the main table that Quartz checks)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'autopilot_qrtz_job_details') THEN
        CREATE TABLE autopilot_qrtz_job_details (
            SCHED_NAME VARCHAR(120) NOT NULL,
            JOB_NAME VARCHAR(200) NOT NULL,
            JOB_GROUP VARCHAR(200) NOT NULL,
            DESCRIPTION VARCHAR(250),
            JOB_CLASS_NAME VARCHAR(250) NOT NULL,
            IS_DURABLE VARCHAR(1) NOT NULL,
            IS_NONCONCURRENT VARCHAR(1) NOT NULL,
            IS_UPDATE_DATA VARCHAR(1) NOT NULL,
            REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
            JOB_DATA BYTEA,
            PRIMARY KEY (SCHED_NAME, JOB_NAME, JOB_GROUP)
        );
        CREATE INDEX idx_autopilot_qrtz_j_grp ON autopilot_qrtz_job_details(SCHED_NAME, JOB_GROUP);
        CREATE INDEX idx_autopilot_qrtz_j_req_recovery ON autopilot_qrtz_job_details(SCHED_NAME, REQUESTS_RECOVERY);
    END IF;
END $$;

-- Note: This script only creates the essential tables. 
-- The full Quartz schema should be created via Liquibase changeset.
-- If you continue to have issues, you may need to:
-- 1. Clear the changeset from DATABASECHANGELOG: 
--    DELETE FROM DATABASECHANGELOG WHERE ID = 'quartz-init-v2' AND AUTHOR = 'quartz';
-- 2. Then restart the application to let Liquibase create all tables
