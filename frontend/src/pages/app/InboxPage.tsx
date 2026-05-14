import MailboxView from "./components/MailboxView";
import Shell from "./components/Shell";

export default function InboxPage() {
  return (
    <Shell>
      <MailboxView title="All Inboxes" />
    </Shell>
  );
}
