-- Additional indexes on outbox_events for efficient polling and deduplication
CREATE INDEX idx_outbox_topic       ON outbox_events(topic)        WHERE published = FALSE;
CREATE INDEX idx_outbox_aggregate   ON outbox_events(aggregate_id) WHERE published = FALSE;

-- Add updated_at to invoices so the bulk UPDATE query can set it
ALTER TABLE invoices
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT NOW();
