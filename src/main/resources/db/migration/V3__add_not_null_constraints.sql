UPDATE book SET created_at = NOW() WHERE created_at IS NULL;
UPDATE book SET last_modified_at = NOW() WHERE last_modified_at IS NULL;

ALTER TABLE book
    ALTER COLUMN title SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN last_modified_at SET NOT NULL;
