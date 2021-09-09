import { Box, Flex, Stack, Text } from "@chakra-ui/react";
import React from "react";
import { Logo } from "../atoms/logo";

export const GlobalFooter: React.FC = () => {
  return (
    <Flex
      align="center"
      height="50px"
      py={2}
      px={4}
      borderTop={1}
      borderStyle="solid"
      borderColor="gray.200"
    >
      <Box>
        <Text>&copy; Sansan, Inc.</Text>
      </Box>
      <Stack flex={1} justify="flex-end" direction="row">
        <Logo />
      </Stack>
    </Flex>
  );
};
