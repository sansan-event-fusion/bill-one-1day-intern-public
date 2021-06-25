import {
  createInvoiceFromObject,
  Invoice,
  InvoiceJson,
} from "../../domain/models/invoice";
import { fetchJSON } from "../../util/fetcher";

const baseURL = process.env.REACT_APP_RECIPIENT_API_ORIGIN;
export async function fetchRecipientInvoices(
  recipientUUID: string
): Promise<Invoice[]> {
  const path = `${baseURL}/api/recipient/recipient/${recipientUUID}/invoices`;

  const options = { method: "GET" };
  const invoiceJsonList = await fetchJSON<InvoiceJson[]>(path, options);
  return invoiceJsonList.map(createInvoiceFromObject);
}

export function fetchRecipientInvoicePdfURL(
  recipientUUID: string,
  invoiceUUID: string
): string {
  return `${baseURL}/api/recipient/recipient/${recipientUUID}/invoices/${invoiceUUID}/pdf-url`;
}
