import type { GmailMessage } from "../types";
import MessageListItem from "./MessageListItem";

type MessageListProps = {
  messages: GmailMessage[];
  selectedMessageKey?: string;
  onSelectMessage: (message: GmailMessage) => void;
};

export default function MessageList({
  messages,
  selectedMessageKey,
  onSelectMessage,
}: MessageListProps) {
  return (
    <div className="list-group list-group-flush">
      {messages.map((message) => {
        const key = `${message.accountId}-${message.id}`;
        return (
          <MessageListItem
            key={key}
            message={message}
            isSelected={selectedMessageKey === key}
            onSelect={() => onSelectMessage(message)}
          />
        );
      })}
    </div>
  );
}
