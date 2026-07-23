import "./CsvImport.css";
import { useRef, useState } from "react";
import useCsvImport from "../hooks/useCsvImport.ts";
import { uuid } from "zod";

export default function CsvImport() {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [isDragging, setIsDragging] = useState(false);
  const [hasInvalidType, setHasInvalidType] = useState(false);
  const { selectFile, leadResponse } = useCsvImport();

  function handleZoneClick(e: React.MouseEvent<HTMLDivElement>) {
    if (e.target === fileInputRef.current) return;
    fileInputRef.current?.click();
  }

  function handleZoneKeyDown(e: React.KeyboardEvent<HTMLDivElement>) {
    if (e.key === "Enter" || e.key === " ") {
      e.preventDefault();
      fileInputRef.current?.click();
    }
  }

  function handleInputChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0] ?? null;

    if (file?.type !== "text/csv") {
      setHasInvalidType(true);
      return;
    }

    void selectFile(file);
  }

  function handleDragOver(e: React.DragEvent<HTMLDivElement>) {
    e.preventDefault();
    setIsDragging(true);
    setHasInvalidType(false);
  }

  function handleDragLeave(e: React.DragEvent<HTMLDivElement>) {
    e.preventDefault();
    setIsDragging(false);
  }

  function handleDrop(e: React.DragEvent<HTMLDivElement>) {
    e.preventDefault();
    setIsDragging(false);
    const file = e.dataTransfer.files?.[0] ?? null;

    if (file.type !== "text/csv") {
      setHasInvalidType(true);
      return;
    }

    setHasInvalidType(false);
    void selectFile(file);
  }

  return (
    <div id="page-csv" className="page-inner">
      <div className="fade-in" style={{ maxWidth: "48rem" }}>
        <div
          className={`drop-zone${isDragging ? " dragover" : ""}${hasInvalidType ? " drop-zone--danger" : ""}`}
          onClick={handleZoneClick}
          onKeyDown={handleZoneKeyDown}
          onDragOver={handleDragOver}
          onDragEnter={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
        >
          <div className="drop-icon">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={1.5}
                d="M4 16v2a2 2 0 002 2h12a2 2 0 002-2v-2M12 12V4m0 8l-3-3m3 3l3-3"
              />
            </svg>
          </div>
          <p>CSV-Datei hochladen</p>
          <p>Datei hierher ziehen oder klicken zum Auswählen</p>
          <small>Unterstützt: .csv · Trennzeichen: , oder ;</small>
          <input
            ref={fileInputRef}
            type="file"
            accept=".csv"
            className="hidden"
            onChange={handleInputChange}
          />
        </div>

        {leadResponse.length > 0 && (
          <>
            <p className="csv-hint-label" style={{ marginTop: "1.5rem" }}>
              Fortschritt ({leadResponse.filter((r) => r.status !== "sending").length}/{leadResponse.length})
            </p>
            <ul className="csv-row-list">
              {leadResponse.map((result) => (
                <li key={uuid.toString()} className={`csv-row-status csv-row-status--${result.status}`}>
                  <span>
                    {result.status === "success"
                      ? `${result.lead.firstname} ${result.lead.lastname}`
                      : `${result.leadRequest.firstname} ${result.leadRequest.lastname}`}
                  </span>
                  <span>
                    {result.status === "sending" && "Wird gesendet…"}
                    {result.status === "success" && "Erfolgreich importiert"}
                    {result.status === "error" && result.reason === "server" && "Fehler beim Import"}
                    {result.status === "error" && result.reason === "validation" && result.messages.join(", ")}
                  </span>
                </li>
              ))}
            </ul>
          </>
        )}

        <div className="csv-hint">
          <p className="csv-hint-label">Erwartetes Format</p>
          <div className="csv-code">
            firstname;lastname;company;phone;email
            <br />
            Max;Mustermann;Muster GmbH;+49 30 123456;max@muster.de;
            <br />
            Erika;Muster;Muster AG;+49 89 654321;erika@ag.de;
          </div>
          <p>
            Spaltenreihenfolge ist beliebig, die Spaltennamen müssen aber
            genau den Feldnamen entsprechen – Trennzeichen , oder ; werden
            automatisch erkannt.
          </p>
        </div>
      </div>
    </div>
  );
}
