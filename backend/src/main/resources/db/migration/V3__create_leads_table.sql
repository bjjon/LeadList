CREATE TABLE lead (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    firstname    VARCHAR(255) NOT NULL,
    lastname     VARCHAR(255) NOT NULL,
    company      VARCHAR(255) NOT NULL,
    phone        VARCHAR(100) NOT NULL,
    email        VARCHAR(100) NOT NULL UNIQUE,
    note         VARCHAR(2000) DEFAULT '',
    status       VARCHAR(50) NOT NULL DEFAULT 'OPEN' REFERENCES status(value),
    created_by   UUID NOT NULL REFERENCES "user"(id),
    assigned_to  UUID REFERENCES "user"(id),
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_lead_status      ON lead(status);
CREATE INDEX idx_lead_created_by  ON lead(created_by);
CREATE INDEX idx_lead_assigned_to ON lead(assigned_to);