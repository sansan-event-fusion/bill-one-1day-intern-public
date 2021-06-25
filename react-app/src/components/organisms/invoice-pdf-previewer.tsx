import { Box } from "@chakra-ui/react";
import React from "react";
import { InvoicePdfPlaceholder } from "./invoice-placeholder";

type InvoicePdfPreviewProps = {
  pdfURL?: string;
};

export const InvoicePdfPreviewer: React.FC<InvoicePdfPreviewProps> = ({
  pdfURL,
}) => {
  if (pdfURL === undefined) return <InvoicePdfPlaceholder />;

  return (
    <Box
      height="100%"
      backgroundColor="#535659"
      border="1px"
      borderColor="gray.300"
      boxSizing="border-box"
    >
      <embed
        src={pdfURL}
        key={pdfURL}
        type="application/pdf"
        width="100%"
        height="100%"
      />
    </Box>
  );
};
