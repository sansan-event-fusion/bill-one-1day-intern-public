import { Box, Flex } from "@chakra-ui/react";
import React from "react";
import { GlobalFooter } from "../organisms/global-footer";
import { SenderHeader } from "../organisms/sender-header";

type SenderLayoutProps = {
  children: React.ReactChild;
};

export const SenderLayout: React.VFC<SenderLayoutProps> = ({ children }) => {
  return (
    <Flex flexFlow="column" minH="100vh">
      <SenderHeader />
      <Box flex={1} px={3}>
        {children}
      </Box>
      <Box mt={4}>
        <GlobalFooter />
      </Box>
    </Flex>
  );
};
