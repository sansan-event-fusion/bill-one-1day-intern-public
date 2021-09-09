import { Center, Image } from "@chakra-ui/react";
import React from "react";

type InvoicePdfPlaceholderProps = {
  pdfURL?: string;
};

export const InvoicePdfPlaceholder: React.FC<InvoicePdfPlaceholderProps> =
  () => {
    return (
      <Center position="relative" height="100%">
        <Center
          position="absolute"
          top={0}
          left={0}
          right={0}
          bottom={0}
          zIndex={10}
          color="white"
          fontWeight="bold"
          fontSize="xl"
          textShadow="2px  2px 10px #666, -2px  2px 10px #666, 2px -2px 10px #666, -2px -2px 10px #666"
          backgroundColor="rgba(128, 128, 128, 0.7)"
          px={4}
          whiteSpace="pre-wrap"
        >
          請求書を選択するとここに表示されます
        </Center>
        <Image
          src="/images/invoice-placeholder.png"
          height="100%"
          width="auto"
          fit="contain"
        />
      </Center>
    );
  };
