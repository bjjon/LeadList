export type PAGE_META_TYPE = {
  title: string;
  subtitle: string;
};

export const PAGE_META: Record<string, PAGE_META_TYPE> = {
  "/": { title: "Leads", subtitle: "Alle Kontakte im Überblick" },
  "/add": { title: "Neuer Lead", subtitle: "Kontakt manuell anlegen" },
  "/csv": { title: "CSV Import", subtitle: "Leads aus Datei importieren" },
};