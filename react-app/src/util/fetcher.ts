export type RequestOptions = RequestInit;

export interface FetchError {
  json?: ErrorResponse;
}

export type ErrorResponse = { error: { message: string } };

export async function fetchURL(
  path: string,
  options: RequestOptions,
  allowedStatusCodes: number[] = []
): Promise<Response> {
  const res: Response = await fetch(path, {
    credentials: "same-origin",
    ...options,
  });

  if (res.ok || allowedStatusCodes.includes(res.status)) {
    return res;
  }

  const error = new Error(`Failed to fetch: ${options.method} ${path}`);
  try {
    (error as FetchError).json = await res.json();
  } catch {
    // do nothing
  }
  throw error;
}

export async function fetchJSON<T>(
  path: string,
  options: RequestOptions
): Promise<T> {
  const res = await fetchURL(path, options);

  return (await res.json()) as T;
}
