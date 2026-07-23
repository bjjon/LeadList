import { z } from "zod";

export const leadSchema = z.object({
  firstname: z.string().trim().min(1, "Vorname darf nicht leer sein"),
  lastname: z.string().trim().min(1, "Nachname darf nicht leer sein"),
  company: z.string().trim().min(1, "Firma darf nicht leer sein"),
  phone: z.string().trim().min(1, "Telefonnummer darf nicht leer sein"),
  email: z.email("Ungültige E-Mail-Adresse").trim().min(1, "E-Mail darf nicht leer sein"),
  note: z.string(),
});

export type LeadFormValues = z.infer<typeof leadSchema>;
