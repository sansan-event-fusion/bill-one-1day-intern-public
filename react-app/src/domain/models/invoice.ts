import { parseISO } from "date-fns";

export type Invoice = {
  invoiceUUID: string;
  recipientFullName: string;
  senderFullName: string;
  registeredAt: Date;
  memo: string;
};

export const createInvoiceFromObject = ({
  registeredAt,
  memo,
  ...rest
}: InvoiceJson): Invoice => {
  return {
    ...rest,
    registeredAt: parseISO(registeredAt),
    memo: memo ?? "",
  };
};

export type InvoiceJson = {
  invoiceUUID: string;
  recipientFullName: string;
  senderFullName: string;
  registeredAt: string;
  memo: string | null;
};
