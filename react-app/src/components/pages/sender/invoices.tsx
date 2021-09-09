import { Box, Flex, Grid, GridItem, Heading, Select } from "@chakra-ui/react";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import {
  fetchSenderInvoicePdfURL,
  fetchSenderInvoices,
} from "../../../api/sender/sender-invoice";
import { SenderInvoice } from "../../../domain/models/sender-invoice";
import { useSenders } from "../../../hooks/sender/useSenders";
import { Loader } from "../../atoms/loader";
import { InvoicePdfPreviewer } from "../../organisms/invoice-pdf-previewer";
import { SenderInvoiceList } from "../../organisms/sender-invoice-list";
import { SenderLayout } from "../../templates/sender-layout";

export const SenderInvoices: React.FC = () => {
  const { senders, loading } = useSenders();

  const [selectedSenderUUID, setSelectedSenderUUID] = useState<string | null>(
    null
  );
  const [invoices, setInvoices] = useState<SenderInvoice[]>([]);
  const [selectedInvoice, setSelectedInvoice] = useState<SenderInvoice | null>(
    null
  );

  useEffect(() => {
    if (!senders.length) return;
    setSelectedSenderUUID(senders[0].senderUUID);
  }, [senders]);

  useEffect(() => {
    if (selectedSenderUUID === null) return;
    const fn = async () => {
      await fetchSenderInvoices(selectedSenderUUID).then(setInvoices);
    };
    fn();
  }, [selectedSenderUUID]);

  const selectedInvoicePdfUrl = useMemo(() => {
    if (selectedSenderUUID === null || selectedInvoice === null) return;

    return fetchSenderInvoicePdfURL(
      selectedSenderUUID,
      selectedInvoice.senderInvoiceUUID
    );
  }, [selectedInvoice, selectedSenderUUID]);

  const handleSelectSenderUUID = useCallback((senderUUID: string) => {
    setSelectedSenderUUID(senderUUID);
    setSelectedInvoice(null);
  }, []);

  return (
    <SenderLayout>
      <>
        <Flex height="100%" flexDirection="column">
          <Heading flex={1} fontSize="2xl" mt={4} ml={4}>
            送付請求書一覧
          </Heading>
          <Grid templateColumns="repeat(8, 1fr)" gap={4} mt={4} px={4}>
            <GridItem colSpan={5}>
              <Select
                flex={2}
                onChange={(e) => handleSelectSenderUUID(e.target.value)}
              >
                {senders.map((s) => (
                  <option key={s.senderUUID} value={s.senderUUID}>
                    {s.fullName}
                  </option>
                ))}
              </Select>
              <Box mt={6}>
                <SenderInvoiceList
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
    </SenderLayout>
  );
};
