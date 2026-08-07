export interface ChatSender {
  id: string;
  firstname: string;
  lastname: string;
}

export type ChatMessage = {
  id: string;
  sender: ChatSender;
  content: string;
  createdAt: string;
};
