CREATE TABLE csv_import (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    imported_by UUID NOT NULL REFERENCES "user"(id),
    row_count   INT NOT NULL DEFAULT 0,
    imported_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_csv_import_imported_by ON csv_import(imported_by);