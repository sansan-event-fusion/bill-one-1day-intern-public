import {
  Flex,
  Grid,
  GridItem,
  Heading,
  Stack,
  useToast,
} from "@chakra-ui/react";
import React, { useCallback, useState } from "react";
import { uploadSenderInvoice } from "../../../api/sender/sender-invoice";
import { useRecipients } from "../../../hooks/recipient/useRecipients";
import { useSenders } from "../../../hooks/sender/useSenders";
import { Loader } from "../../atoms/loader";
import { InvoicePdfPreviewer } from "../../organisms/invoice-pdf-previewer";
import { SenderSendInvoiceForm } from "../../organisms/sender-send-invoice-form";
import { SenderLayout } from "../../templates/sender-layout";

export const SenderSendInvoice: React.FC = () => {
  const toast = useToast();

  const { recipients, loading: recipientsLoading } = useRecipients();
  const { senders, loading: sendersLoading } = useSenders();
  const [previewPdfURL, setPreviewPdfURL] = useState<string>();
  const [loading, setLoading] = useState(false);

  const handleSelectInvoiceFile = useCallback((file: File | null) => {
    if (file === null) {
      return setPreviewPdfURL(undefined);
    }

    const reader = new FileReader();

    reader.onload = (event) => {
      const dataURL = event.target?.result;
      setPreviewPdfURL(dataURL as unknown as string);
    };
    reader.readAsDataURL(file);
  }, []);

  const handleSubmit = useCallback(
    async (
      recipientUUID: string,
      senderUUID: string,
      invoice: File,
      memo?: string
    ) => {
      setLoading(true);
      await uploadSenderInvoice({
        recipientUUID,
        senderUUID,
        invoice,
        memo,
      })
        .then(() =>
          toast({
            title: "請求書を送付しました",
            status: "success",
            duration: 5000,
            isClosable: true,
            position: "bottom-left",
          })
        )
        .catch((error) => {
          toast({
            title: "請求書の送付に失敗しました",
            status: "error",
            duration: 5000,
            isClosable: true,
            position: "bottom-left",
          });
          throw error;
        })
        .finally(() => setLoading(false));
    },
    [toast]
  );

  return (
    <SenderLayout>
      <>
        <Stack height="100%" spacing={4}>
          <Heading mt={4} ml={4} fontSize="2xl">
            請求書送付
          </Heading>
          <Grid templateColumns="repeat(8, 1fr)" gap={6} mt={4} px={4}>
            <GridItem colSpan={5}>
              <SenderSendInvoiceForm
                recipients={recipients}
                senders={senders}
                onChangeInvoiceFile={handleSelectInvoiceFile}
                onSubmit={handleSubmit}
              />
            </GridItem>
            <GridItem colSpan={3}>
              <Flex
                direction="column"
                position="relative"
                zIndex={100}
                width="100%"
                height="75vh"
                background="#f8f8f8"
              >
                <InvoicePdfPreviewer pdfURL={previewPdfURL} />
              </Flex>
            </GridItem>
          </Grid>
        </Stack>
        {(recipientsLoading || sendersLoading || loading) && <Loader />}
      </>
    </SenderLayout>
  );
};
