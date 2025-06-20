CREATE SCHEMA IF NOT EXISTS document;
-- drop schema public if exists
DROP SCHEMA IF EXISTS public CASCADE;

DO $$
DECLARE
    db_list TEXT;
BEGIN
    SELECT string_agg(datname, ',') INTO db_list FROM pg_database WHERE NOT datistemplate;
    EXECUTE 'ALTER ROLE test SET search_path TO public,' || db_list;
END$$;