import { Sender } from "../../domain/models/sender";
import { fetchJSON } from "../../util/fetcher";

const baseURL = process.env.REACT_APP_SENDER_API_ORIGIN;

export async function fetchSenders(): Promise<Sender[]> {
  const path = `${baseURL}/api/sender/senders`;

  const options = { method: "GET" };
  const senders = await fetchJSON<Sender[]>(path, options);
  return senders;
}
