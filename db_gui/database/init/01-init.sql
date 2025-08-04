-- Initial database setup
-- This script runs when the PostgreSQL container starts for the first time

-- Create audit schema for change tracking
CREATE SCHEMA IF NOT EXISTS audit;

-- Create function to track changes (will be used by triggers)
CREATE OR REPLACE FUNCTION audit.audit_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit.customer_audit (
            customer_id, action, changed_by, changed_at, 
            old_values, new_values
        ) VALUES (
            NEW.id, 'INSERT', 'system', NOW(), 
            NULL, row_to_json(NEW)
        );
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit.customer_audit (
            customer_id, action, changed_by, changed_at,
            old_values, new_values
        ) VALUES (
            NEW.id, 'UPDATE', 'system', NOW(),
            row_to_json(OLD), row_to_json(NEW)
        );
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO audit.customer_audit (
            customer_id, action, changed_by, changed_at,
            old_values, new_values
        ) VALUES (
            OLD.id, 'DELETE', 'system', NOW(),
            row_to_json(OLD), NULL
        );
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;
