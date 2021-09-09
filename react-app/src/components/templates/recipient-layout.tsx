import { Box, Flex } from "@chakra-ui/react";
import React from "react";
import { GlobalFooter } from "../organisms/global-footer";
import { RecipientHeader } from "../organisms/recipient-header";

type RecipientLayoutProps = {
  children: React.ReactChild;
};

export const RecipientLayout: React.VFC<RecipientLayoutProps> = ({
  children,
}) => {
  return (
    <Flex flexFlow="column" minH="100vh">
      <RecipientHeader />
      <Box flex={1} px={3}>
        {children}
      </Box>
      <Box mt={4}>
        <GlobalFooter />
      </Box>
    </Flex>
  );
};
