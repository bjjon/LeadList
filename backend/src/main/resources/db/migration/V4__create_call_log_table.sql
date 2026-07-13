CREATE TABLE call_log (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id   UUID NOT NULL REFERENCES lead(id) ON DELETE CASCADE,
    user_id   UUID NOT NULL REFERENCES "user"(id),
    result    VARCHAR(50) NOT NULL CHECK (result IN ('REACHED', 'NOT_REACHED')),
    notes     TEXT,
    called_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_call_log_lead_id ON call_log(lead_id);
CREATE INDEX idx_call_log_user_id ON call_log(user_id);