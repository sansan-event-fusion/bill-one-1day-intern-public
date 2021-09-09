import { Recipient } from "../../domain/models/recipient";
import { fetchJSON } from "../../util/fetcher";

const baseURL = process.env.REACT_APP_RECIPIENT_API_ORIGIN;

export async function fetchRecipients(): Promise<Recipient[]> {
  const path = `${baseURL}/api/recipient/recipients`;

  const options = { method: "GET" };
  const recipients = await fetchJSON<Recipient[]>(path, options);
  return recipients;
}
