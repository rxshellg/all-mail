import { useParams } from "react-router-dom";
import Shell from "./components/Shell";
import MailboxView from "./components/MailboxView";

export default function AccountInboxPage() {
  const { accountId } = useParams();
  const parsedAccountId = Number(accountId);

  return (
    <Shell>
      <MailboxView title="Account Inbox" accountId={parsedAccountId} />
    </Shell>
  );
}
