import { useEffect, useMemo, useState } from "react";
import { apiGet } from "../utils/api";
import MessageDetail from "./MessageDetail";
import MessageList from "./MessageList";
import type { GmailAccountError, GmailInboxResponse, GmailMessage } from "../types";
import "./MailboxView.css";

type MailboxViewProps = {
  title: string;
  accountId?: number;
};

export default function MailboxView({ title, accountId }: MailboxViewProps) {
  const [messages, setMessages] = useState<GmailMessage[]>([]);
  const [errors, setErrors] = useState<GmailAccountError[]>([]);
  const [selectedMessage, setSelectedMessage] = useState<GmailMessage | null>(
    null,
  );
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiGet<GmailInboxResponse>("/api/gmail/messages")
      .then(({ messages, errors }) => {
        setMessages(messages);
        setErrors(errors);
      })
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const visibleMessages = useMemo(
    () =>
      accountId ? messages.filter((m) => m.accountId === accountId) : messages,
    [messages, accountId],
  );

  useEffect(() => {
    if (!visibleMessages.length) return void setSelectedMessage(null);

    const selectedMessageIsStillVisible = visibleMessages.some(
      (message) =>
        selectedMessage &&
        message.id === selectedMessage.id &&
        message.accountId === selectedMessage.accountId,
    );

    if (!selectedMessage || !selectedMessageIsStillVisible) {
      setSelectedMessage(visibleMessages[0]);
    }
  }, [visibleMessages, selectedMessage]);

  const selectedMessageKey = selectedMessage
    ? `${selectedMessage.accountId}-${selectedMessage.id}`
    : undefined;

  return (
    <div className="mailbox-page">
      <div className="mailbox-header mb-4">
        <h1 className="fw-bold mb-2">{title}</h1>
      </div>

      {errors.length > 0 && (
        <div className="mailbox-errors rounded-4 mb-4">
          {errors.map((error) => (
            <p className="mb-1" key={error.accountId}>
              <strong>{error.accountEmail}</strong>: {error.message}
            </p>
          ))}
        </div>
      )}

      <div className="mailbox-layout card border-0 shadow-sm rounded-4">
        <div className="mailbox-list-panel">
          {loading ? (
            <p className="text-secondary p-4 mb-0">Loading messages...</p>
          ) : !visibleMessages.length ? (
            <p className="text-secondary p-4 mb-0">No messages found.</p>
          ) : (
            <MessageList
              messages={visibleMessages}
              selectedMessageKey={selectedMessageKey}
              onSelectMessage={setSelectedMessage}
            />
          )}
        </div>

        <MessageDetail message={selectedMessage} />
      </div>
    </div>
  );
}
