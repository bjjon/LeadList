-- -------------------------------------------------------------
-- USER
-- Passwort-Hash entspricht "password123" (BCrypt, cost 12)
-- -------------------------------------------------------------
INSERT INTO "user" (id, firstname, lastname, email, password, created_at) VALUES
    ('4994802c-b652-4431-8b27-921e76093284', 'Max', 'Mustermann',  'max@example.com',    '$2a$12$13QTcucQJcSo2DwSntr9OOQYpB8DvHgph//LO.XfunnFZxPLACTzS', NOW() - INTERVAL '30 days'),
    ('8b5d9619-0acb-457b-8594-ae20db513cdc', 'Lisa','Schneider',  'lisa@example.com',   '$2a$12$13QTcucQJcSo2DwSntr9OOQYpB8DvHgph//LO.XfunnFZxPLACTzS', NOW() - INTERVAL '25 days'),
    ('28515cdb-370f-466f-bf95-e0042e07e540', 'Tom', 'Fischer',     'tom@example.com',    '$2a$12$13QTcucQJcSo2DwSntr9OOQYpB8DvHgph//LO.XfunnFZxPLACTzS', NOW() - INTERVAL '20 days')
ON CONFLICT (id) DO NOTHING;


-- -------------------------------------------------------------
-- LEAD  (10 Einträge, gemischte Status & Zuweisungen)
-- -------------------------------------------------------------
INSERT INTO lead (id, firstname, lastname, company, phone, email, status, created_by, assigned_to, created_at, updated_at) VALUES
    ('02aaf375-cfd5-4d29-9315-7e186d08e8d2', 'Markus', 'Hoffmann', 'TechCorp GmbH',         '+49 30 1234567',   'hoffmann@mail.de', 'OPEN',        '4994802c-b652-4431-8b27-921e76093284', '4994802c-b652-4431-8b27-921e76093284', NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),
    ('f1f8312e-f94e-4dbc-b6b2-898af5c3c2b6', 'Sabine', 'Klein',    'Klein Logistik',        '+49 89 7654321',   'klein@mail.de',    'OPEN',        '4994802c-b652-4431-8b27-921e76093284', '8b5d9619-0acb-457b-8594-ae20db513cdc', NOW() - INTERVAL '9 days',  NOW() - INTERVAL '9 days'),
    ('806e1c0d-59b7-4725-b4a4-feaeacff9c6b', 'Peter', 'Wolf',      'Wolf & Partner',        '+49 40 9876543',   'wolf@mail.de',     'REACHED',     '8b5d9619-0acb-457b-8594-ae20db513cdc', '8b5d9619-0acb-457b-8594-ae20db513cdc', NOW() - INTERVAL '8 days',  NOW() - INTERVAL '1 day'),
    ('7ddc4d61-7118-4f20-8798-446c7406e431', 'Julia', 'Roth',      'Roth Design',           '+49 221 555012',   'roth@mail.de',     'NOT_REACHED', '8b5d9619-0acb-457b-8594-ae20db513cdc', '28515cdb-370f-466f-bf95-e0042e07e540', NOW() - INTERVAL '7 days',  NOW() - INTERVAL '2 days'),
    ('37924cef-f394-4930-973e-0e9e49916964', 'Andrea', 'Bauer',    'Bauer Consulting',      '+49 711 223344',   'bauer@mail.de',    'OPEN',        '28515cdb-370f-466f-bf95-e0042e07e540', '4994802c-b652-4431-8b27-921e76093284', NOW() - INTERVAL '6 days',  NOW() - INTERVAL '6 days'),
    ('bb213b35-b011-43d4-bd2e-ec05ff99d198', 'Claudia', 'Maier',   'Maier & Söhne GmbH',    '+49 351 998877',   'maier@mail.de',    'REACHED',     '4994802c-b652-4431-8b27-921e76093284', '8b5d9619-0acb-457b-8594-ae20db513cdc', NOW() - INTERVAL '5 days',  NOW() - INTERVAL '12 hours'),
    ('d0165c1e-05c4-4857-9948-fce1f785a98f', 'Stefan', 'Braun',    'Braun Elektro',         '+49 621 445566',   'braun@mail.de',    'NOT_REACHED', '28515cdb-370f-466f-bf95-e0042e07e540', '28515cdb-370f-466f-bf95-e0042e07e540', NOW() - INTERVAL '4 days',  NOW() - INTERVAL '4 days'),
    ('fa66faa2-4e72-4c28-ab91-b1c189d7c533', 'Monika', 'Wagner',   'Wagner Immobilien',     '+49 511 667788',   'wagner@mail.de',   'OPEN',        '8b5d9619-0acb-457b-8594-ae20db513cdc', '4994802c-b652-4431-8b27-921e76093284', NOW() - INTERVAL '3 days',  NOW() - INTERVAL '3 days'),
    ('327c8cca-6112-445c-835f-8f70a6edda12', 'Klaus', 'Richter',   'Richter Maschinenbau',  '+49 911 334455',   'richter@mail.de',  'REACHED',     '4994802c-b652-4431-8b27-921e76093284', '28515cdb-370f-466f-bf95-e0042e07e540', NOW() - INTERVAL '2 days',  NOW() - INTERVAL '6 hours'),
    ('7de80d59-d795-4a8e-ac0b-17243aec64ac', 'Eva', 'Schulz',      'Schulz & Partner KG',   '+49 201 112233',   'schulz@mail.de',   'NOT_REACHED', '28515cdb-370f-466f-bf95-e0042e07e540', '8b5d9619-0acb-457b-8594-ae20db513cdc', NOW() - INTERVAL '1 day',   NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

-- -------------------------------------------------------------
-- CALL_LOG  (Anrufversuche für ausgewählte Leads)
-- -------------------------------------------------------------
INSERT INTO call_log (id, lead_id, user_id, result, notes, called_at) VALUES
    ('14859c53-e68a-44a7-b143-3c7ff7a6cae0', '806e1c0d-59b7-4725-b4a4-feaeacff9c6b', '8b5d9619-0acb-457b-8594-ae20db513cdc', 'REACHED',     'Hat zugesagt, meldet sich morgen.',         NOW() - INTERVAL '1 day'),
    ('fc399db7-0c9c-4f59-b8ee-2831b3a2b20a', '7ddc4d61-7118-4f20-8798-446c7406e431', '28515cdb-370f-466f-bf95-e0042e07e540', 'NOT_REACHED', 'Mailbox. Rückruf hinterlassen.',            NOW() - INTERVAL '2 days'),
    ('61881187-2a5a-4016-ae19-60969f364a81', 'bb213b35-b011-43d4-bd2e-ec05ff99d198', '8b5d9619-0acb-457b-8594-ae20db513cdc', 'REACHED',     'Termin für nächste Woche vereinbart.',      NOW() - INTERVAL '12 hours'),
    ('1ab0b16a-2765-45ca-8db6-e020f45bbb28', 'd0165c1e-05c4-4857-9948-fce1f785a98f', '28515cdb-370f-466f-bf95-e0042e07e540', 'NOT_REACHED', 'Kein Anschluss unter dieser Nummer.',       NOW() - INTERVAL '4 days'),
    ('6240ef2a-f1bb-43eb-a020-1712ae438115', '327c8cca-6112-445c-835f-8f70a6edda12', '4994802c-b652-4431-8b27-921e76093284', 'REACHED',     'Interesse vorhanden, Angebot per Mail.',    NOW() - INTERVAL '6 hours'),
    ('8bb2889d-c1b7-4a4c-9634-047e22b39fe4', '7de80d59-d795-4a8e-ac0b-17243aec64ac', '8b5d9619-0acb-457b-8594-ae20db513cdc', 'NOT_REACHED', 'Beschäftigt, bittet um Rückruf morgen.',    NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

-- -------------------------------------------------------------
-- CSV_IMPORT  (ein Beispiel-Import)
-- -------------------------------------------------------------
INSERT INTO csv_import (id, imported_by, row_count, imported_at) VALUES
    ('52b021d3-1ed6-4df2-a475-97ace96e7e2d', '4994802c-b652-4431-8b27-921e76093284', 10, NOW() - INTERVAL '10 days')
ON CONFLICT (id) DO NOTHING;