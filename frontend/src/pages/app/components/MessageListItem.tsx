import type { GmailMessage } from "../types";
import { formatMessageDate, getSenderName } from "../utils/mailFormatters";
import "./MessageListItem.css";

type MessageListItemProps = {
  message: GmailMessage;
  isSelected: boolean;
  onSelect: () => void;
};

export default function MessageListItem({
  message,
  isSelected,
  onSelect,
}: MessageListItemProps) {
  const classes = [
    "list-group-item mailbox-message",
    message.unread && "unread",
    isSelected && "selected",
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <article className={classes} onClick={onSelect}>
      <div className="d-flex justify-content-between gap-3">
        <div className="mailbox-message-main">
          <div className="d-flex align-items-center gap-2 mb-1">
            <strong>{getSenderName(message.from)}</strong>
          </div>
          <p className="fw-semibold mb-1">
            {message.subject || "(No subject)"}
          </p>
          <p className="text-secondary mb-0 mailbox-snippet">{message.snippet}</p>
        </div>
        <small className="text-secondary mailbox-date">
          {formatMessageDate(message.receivedAt)}
        </small>
      </div>
    </article>
  );
}
