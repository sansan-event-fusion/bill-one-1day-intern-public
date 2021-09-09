import { Box, Flex, Grid, GridItem, Heading, Select } from "@chakra-ui/react";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import {
  fetchRecipientInvoicePdfURL,
  fetchRecipientInvoices,
} from "../../../api/recipient/invoice";
import { Invoice } from "../../../domain/models/invoice";
import { useRecipients } from "../../../hooks/recipient/useRecipients";
import { Loader } from "../../atoms/loader";
import { InvoicePdfPreviewer } from "../../organisms/invoice-pdf-previewer";
import { RecipientInvoiceList } from "../../organisms/recipient-invoice-list";
import { RecipientLayout } from "../../templates/recipient-layout";

export const RecipientInvoices: React.FC = () => {
  const { recipients, loading } = useRecipients();

  const [selectedRecipientUUID, setSelectedRecipientUUID] = useState<
    string | null
  >(null);
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [selectedInvoice, setSelectedInvoice] = useState<Invoice | null>(null);

  useEffect(() => {
    if (!recipients.length) return;
    setSelectedRecipientUUID(recipients[0].recipientUUID);
  }, [recipients]);

  useEffect(() => {
    if (selectedRecipientUUID === null) return;
    const fn = async () => {
      await fetchRecipientInvoices(selectedRecipientUUID).then(setInvoices);
    };
    fn();
  }, [selectedRecipientUUID]);

  const selectedInvoicePdfUrl = useMemo(() => {
    if (selectedRecipientUUID === null || selectedInvoice === null) return;

    return fetchRecipientInvoicePdfURL(
      selectedRecipientUUID,
      selectedInvoice.invoiceUUID
    );
  }, [selectedInvoice, selectedRecipientUUID]);

  const handleSelectRecipientUUID = useCallback((senderUUID: string) => {
    setSelectedRecipientUUID(senderUUID);
    setSelectedInvoice(null);
  }, []);

  return (
    <RecipientLayout>
      <>
        <Flex height="100%" flexDirection="column">
          <Heading mt={4} ml={4} fontSize="2xl">
            受領請求書一覧
          </Heading>
          <Grid templateColumns="repeat(8, 1fr)" gap={4} mt={4} px={4}>
            <GridItem colSpan={5}>
              <Select
                flex={2}
                onChange={(e) => handleSelectRecipientUUID(e.target.value)}
              >
                {recipients.map((r) => (
                  <option key={r.recipientUUID} value={r.recipientUUID}>
                    {r.fullName}
                  </option>
                ))}
              </Select>
              <Box mt={6}>
                <RecipientInvoiceList
                  invoices={invoices}
                  onClickInvoice={setSelectedInvoice}
                />
              </Box>
            </GridItem>
            <GridItem colSpan={3}>
              <Flex
                direction="column"
                position="sticky"
                top="1em"
                width="100%"
                height="80vh"
                background="#f8f8f8"
              >
                <InvoicePdfPreviewer pdfURL={selectedInvoicePdfUrl} />
              </Flex>
            </GridItem>
          </Grid>
        </Flex>
        {loading && <Loader />}
      </>
    </RecipientLayout>
  );
};
