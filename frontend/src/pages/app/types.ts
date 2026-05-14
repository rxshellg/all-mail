export type CurrentUser = {
  name: string;
  email: string;
  pictureUrl: string;
};

export type ConnectedAccount = {
  id: number;
  provider: string;
  emailAddress: string;
  displayName: string;
  pictureUrl: string;
  active: boolean;
};

export type GmailMessage = {
  id: string;
  threadId: string;
  accountId: number;
  accountEmail: string;
  accountDisplayName: string;
  from: string;
  subject: string;
  snippet: string;
  receivedAt: string;
  unread: boolean;
};

export type GmailAccountError = {
  accountId: number;
  accountEmail: string;
  provider: string;
  message: string;
  reconnectRequired: boolean;
};

export type GmailInboxResponse = {
  messages: GmailMessage[];
  errors: GmailAccountError[];
};
