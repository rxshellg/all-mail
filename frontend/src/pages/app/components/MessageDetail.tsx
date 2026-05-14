import type { GmailMessage } from "../types";
import { formatMessageDate, getSenderName } from "../utils/mailFormatters";
import "./MessageDetail.css";

type MessageDetailProps = {
  message: GmailMessage | null;
};

export default function MessageDetail({ message }: MessageDetailProps) {
  if (!message) {
    return (
      <section className="message-empty">
        <div>
          <i className="bi bi-envelope-open" />
          <p className="mb-0 mt-3">Select a message to preview it.</p>
        </div>
      </section>
    );
  }

  return (
    <section className="message-detail">
      <div className="message-header">
        <span className="badge rounded-pill text-bg-light border">
          {message.accountEmail}
        </span>

        <h2 className="h4 fw-bold mt-3 mb-2">
          {message.subject || "(No subject)"}
        </h2>

        <div className="text-secondary small">
          <span>{getSenderName(message.from)}</span>
          <span className="mx-2">•</span>
          <span>{formatMessageDate(message.receivedAt)}</span>
        </div>
      </div>

      <div className="message-body">
        <p>{message.snippet || "No preview available for this message yet."}</p>
      </div>
    </section>
  );
}
