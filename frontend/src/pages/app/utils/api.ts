const API_BASE_URL = "http://localhost:8080";

export async function apiGet<T>(path: string): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    credentials: "include",
  });

  if (!response.ok) throw new Error(`Request failed: ${path}`);

  return response.json();
}
