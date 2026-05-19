export const API_BASE_URL = "http://localhost:8080";

async function apiFetch(
  path: string,
  options?: RequestInit,
): Promise<Response> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    credentials: "include",
    ...options,
  });
  if (!response.ok) throw new Error(`Request failed: ${path}`);
  return response;
}

export const apiGet = <T>(path: string): Promise<T> =>
  apiFetch(path).then((r) => r.json());

export const apiDelete = (path: string): Promise<void> =>
  apiFetch(path, { method: "DELETE" }).then(() => undefined);
