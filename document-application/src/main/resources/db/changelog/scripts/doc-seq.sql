DO $$
DECLARE
max_id BIGINT;
BEGIN
-- Calculate the new start value based on existing data
SELECT coalesce(MAX(document_id),0) INTO max_id FROM document;
max_id := max_id + 1; -- Increment by 1

-- Check if the sequence already exists
IF EXISTS (SELECT 1 FROM information_schema.sequences WHERE sequence_name = 'document_seq') THEN
-- Modify the existing sequence to increment by 1
EXECUTE 'ALTER SEQUENCE document_seq INCREMENT BY 1 RESTART WITH ' || max_id::text;
ELSE
-- Create the sequence if it doesn't exist, incrementing by 1
EXECUTE 'CREATE SEQUENCE document_seq INCREMENT BY 1 START ' || max_id::text;
END IF;
END $$;