export function formatMessageDate(receivedAt: string) {
  const date = new Date(receivedAt);

  if (Number.isNaN(date.getTime())) return "";

  const today = new Date();
  const isToday =
    date.getDate() === today.getDate() &&
    date.getMonth() === today.getMonth() &&
    date.getFullYear() === today.getFullYear();

  return date.toLocaleString(
    [],
    isToday
      ? { hour: "numeric", minute: "2-digit" }
      : { day: "numeric", month: "short" },
  );
}

export function getSenderName(from: string) {
  if (!from) return "Unknown sender";
  return from.match(/^"?([^"<]+?)"?\s*</)?.[1]?.trim() || from;
}
