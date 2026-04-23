CREATE TABLE fee_structures (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    grade_level VARCHAR(20)    NOT NULL,
    fee_type    VARCHAR(20)    NOT NULL,
    amount      NUMERIC(10,2)  NOT NULL CHECK (amount >= 0),
    active      BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW(),
    UNIQUE (grade_level, fee_type)
);

CREATE TABLE invoices (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id        UUID           NOT NULL,
    fee_structure_id  UUID           NOT NULL REFERENCES fee_structures(id),
    amount            NUMERIC(10,2)  NOT NULL CHECK (amount >= 0),
    due_date          DATE           NOT NULL,
    status            VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    created_at        TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_invoices_student_id ON invoices(student_id);
CREATE INDEX idx_invoices_status     ON invoices(status);

CREATE TABLE payments (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    invoice_id       UUID           NOT NULL REFERENCES invoices(id),
    paid_amount      NUMERIC(10,2)  NOT NULL CHECK (paid_amount > 0),
    paid_at          TIMESTAMP      NOT NULL DEFAULT NOW(),
    payment_method   VARCHAR(50)    NOT NULL,
    transaction_id   VARCHAR(100)   NOT NULL UNIQUE
);

CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);

CREATE TABLE outbox_events (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    topic         VARCHAR(100) NOT NULL,
    aggregate_id  VARCHAR(100) NOT NULL,
    payload       TEXT         NOT NULL,
    published     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    published_at  TIMESTAMP
);

CREATE INDEX idx_outbox_unpublished ON outbox_events(created_at) WHERE published = FALSE;
