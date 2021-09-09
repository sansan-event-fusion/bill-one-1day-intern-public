import {
  createSenderInvoiceFromObject,
  SenderInvoice,
  SenderInvoiceJson,
} from "../../domain/models/sender-invoice";
import { fetchJSON, fetchURL, RequestOptions } from "../../util/fetcher";

const baseURL = process.env.REACT_APP_SENDER_API_ORIGIN;

type UploadSenderInvoiceRequest = {
  invoice: File;
  recipientUUID: string;
  senderUUID: string;
  memo?: string;
};

export async function fetchSenderInvoices(
  senderUUID: string
): Promise<SenderInvoice[]> {
  const path = `${baseURL}/api/sender/sender/${senderUUID}/sender-invoices`;

  const options = { method: "GET" };
  const invoiceJsonList = await fetchJSON<SenderInvoiceJson[]>(path, options);
  return invoiceJsonList.map(createSenderInvoiceFromObject);
}

export function fetchSenderInvoicePdfURL(
  senderUUID: string,
  senderInvoiceUUID: string
): string {
  return `${baseURL}/api/sender/sender/${senderUUID}/sender-invoices/${senderInvoiceUUID}/pdf-url`;
}

export async function uploadSenderInvoice(
  request: UploadSenderInvoiceRequest
): Promise<void> {
  const { invoice, recipientUUID, senderUUID, memo } = request;
  const body = new FormData();
  body.append("invoice", invoice);
  body.append("recipientUUID", recipientUUID);
  body.append("senderUUID", senderUUID);
  if (memo !== undefined) {
    body.append("memo", memo);
  }

  const path = `${baseURL}/api/sender/sender-invoices`;
  const options: RequestOptions = {
    body,
    method: "POST",
  };

  await fetchURL(path, options);
  return;
}
