export type LeadStatus = {
  value: string;
  label: string;
  color: string;
};

export interface LeadUser {
  id: string;
  firstname: string;
  lastname: string;
}

export type Lead = {
  id: string;
  firstname: string;
  lastname: string;
  company: string;
  phone: string;
  email: string;
  note: string;
  status: LeadStatus;
  createdBy: LeadUser;
  assignedTo: LeadUser;
  createdAt: string;
};