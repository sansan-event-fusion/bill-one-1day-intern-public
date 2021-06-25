import { parseISO } from "date-fns";

export type SenderInvoice = {
  senderInvoiceUUID: string;
  recipientFullName: string;
  senderFullName: string;
  registeredAt: Date;
  memo: string;
};

export const createSenderInvoiceFromObject = ({
  registeredAt,
  memo,
  ...rest
}: SenderInvoiceJson): SenderInvoice => {
  return {
    ...rest,
    registeredAt: parseISO(registeredAt),
    memo: memo ?? "",
  };
};

export type SenderInvoiceJson = {
  senderInvoiceUUID: string;
  recipientFullName: string;
  senderFullName: string;
  registeredAt: string;
  memo: string | null;
};
