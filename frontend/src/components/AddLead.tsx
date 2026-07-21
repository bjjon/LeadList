import "./AddLead.css";
import { useLeads } from "../context/LeadContext.tsx";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { leadFormSchema, type LeadFormValues } from "../schemas/leadFormSchema.ts";
import { useNavigate } from "react-router-dom";
import FieldError from "./FieldError.tsx";

export default function AddLead() {
  const { addLead } = useLeads();
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LeadFormValues>({
    resolver: zodResolver(leadFormSchema),
    defaultValues: {
      firstname: "",
      lastname: "",
      company: "",
      phone: "",
      email: "",
      note: "",
    },
  });

  const onValid = async (data: LeadFormValues) => {
    await addLead(data);
    navigate("/");
  };

  return (
    <div id="page-add" className="page-inner">
      <div className="card fade-in" style={{ maxWidth: "36rem" }}>
        <div className="card-head">
          <h3>Lead-Daten eingeben</h3>
          <p>Pflichtfelder sind mit * markiert</p>
        </div>
        <form id="addForm" className="card-body" onSubmit={handleSubmit(onValid)}>
          <div className="field-grid">
            <div className="field" style={{ margin: "0" }}>
              <label htmlFor="newFirstname">Vorname *</label>
              <input
                type="text"
                id="newFirstname"
                placeholder="Max"
                {...register("firstname")}
              />
              <FieldError message={errors.firstname?.message} />
            </div>
            <div className="field" style={{ margin: "0" }}>
              <label htmlFor="newLastname">Nachname *</label>
              <input
                type="text"
                id="newLastname"
                placeholder="Mustermann"
                {...register("lastname")}
              />
              <FieldError message={errors.lastname?.message} />
            </div>
            <div className="field" style={{ margin: "0" }}>
              <label htmlFor="newPhone">Telefon *</label>
              <input
                type="tel"
                id="newPhone"
                placeholder="+49 30 123456"
                {...register("phone")}
              />
              <FieldError message={errors.phone?.message} />
            </div>
            <div className="field" style={{ margin: "0" }}>
              <label htmlFor="newCompany">Firma *</label>
              <input
                type="text"
                id="newCompany"
                placeholder="Muster GmbH"
                {...register("company")}
              />
              <FieldError message={errors.company?.message} />
            </div>
            <div className="field" style={{ margin: "0" }}>
              <label htmlFor="newEmail">E-Mail *</label>
              <input
                type="text"
                id="newEmail"
                placeholder="max@muster.de"
                {...register("email")}
              />
              <FieldError message={errors.email?.message} />
            </div>
          </div>
          <div className="field" style={{ marginTop: "1.25rem" }}>
            <label htmlFor="newNote">Notiz</label>
            <textarea
              id="newNote"
              placeholder="Optionale Notiz …"
              {...register("note")}
            ></textarea>
          </div>
          <div id="addSuccess" className="hidden">
            <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="2"
                d="M5 13l4 4L19 7"
              />
            </svg>
            Lead wurde erfolgreich angelegt.
          </div>
          <div className="btn-row">
            <button type="submit" className="btn-accent">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2.5"
                  d="M12 4v16m8-8H4"
                />
              </svg>
              Lead anlegen
            </button>
            <button
              type="button"
              className="btn-ghost"
              onClick={() => navigate("/")}
            >
              Abbrechen
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
