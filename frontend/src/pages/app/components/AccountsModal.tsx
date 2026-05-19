import { useEffect, useState } from "react";
import { apiDelete } from "../utils/api";
import type { ConnectedAccount } from "../types";
import "./AccountsModal.css";

type AccountsModalProps = {
  accounts: ConnectedAccount[];
  onClose: () => void;
  refreshAccounts: () => Promise<void>;
};

export default function AccountsModal({
  accounts,
  onClose,
  refreshAccounts,
}: AccountsModalProps) {
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    refreshAccounts().finally(() => setLoading(false));
  }, []);

  const addGoogleAccount = () => {
    window.location.href = "http://localhost:8080/api/accounts/connect/google";
  };

  const removeAccount = (accountId: number) => {
    apiDelete(`/api/accounts/${accountId}`)
      .then(() => refreshAccounts())
      .catch(console.error);
  };

  const reconnectAccount = (accountId: number) => {
    window.location.href = `http://localhost:8080/api/accounts/${accountId}/reconnect/google`;
  };

  return (
    <div className="accounts-modal-backdrop">
      <section className="accounts-modal bg-white rounded-4 shadow">
        <div className="d-flex justify-content-between align-items-start mb-4">
          <div>
            <h2 className="h4 fw-bold mb-1">Manage accounts</h2>
            <p className="text-secondary mb-0">
              Add, remove, or reconnect the inboxes linked to AllMail.
            </p>
          </div>

          <button className="btn border-0" type="button" onClick={onClose}>
            <i className="bi bi-x-lg"></i>
          </button>
        </div>

        {loading ? (
          <p className="text-secondary">Loading accounts...</p>
        ) : (
          <div className="d-flex flex-column gap-3">
            {accounts.map((account) => (
              <div
                className="accounts-modal-row rounded-4 border p-3"
                key={account.id}
              >
                <div>
                  <p className="fw-semibold mb-1">{account.emailAddress}</p>
                  <small
                    className={
                      account.needsReconnect
                        ? "text-warning fw-semibold"
                        : "text-success fw-semibold"
                    }
                  >
                    {account.needsReconnect ? "Needs reconnect" : "Connected"}
                  </small>
                </div>

                <div className="d-flex gap-2">
                  {account.needsReconnect && (
                    <button
                      className="btn btn-outline-primary btn-sm"
                      type="button"
                      onClick={() => reconnectAccount(account.id)}
                    >
                      Reconnect
                    </button>
                  )}

                  <button
                    className="btn btn-outline-danger btn-sm"
                    type="button"
                    onClick={() => removeAccount(account.id)}
                  >
                    Remove
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        <button
          className="btn btn-primary w-100 mt-4"
          type="button"
          onClick={addGoogleAccount}
        >
          <i className="bi bi-plus-circle me-2"></i>
          Add Google account
        </button>
      </section>
    </div>
  );
}
