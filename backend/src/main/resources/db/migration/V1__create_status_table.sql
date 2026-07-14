CREATE TABLE status (
    value VARCHAR(50) PRIMARY KEY,
    label VARCHAR(100) NOT NULL,
    color VARCHAR(50) NOT NULL
);

INSERT INTO status (value, label, color) VALUES
    ('OPEN',        'Offen',          '#64748b'),
    ('IN_PROGRESS', 'In Bearbeitung', '#fbbf24'),
    ('REACHED',     'Erreicht',       '#4ade80'),
    ('NOT_REACHED', 'Nicht erreicht', '#f87171');