import { Flex, Link, Stack } from "@chakra-ui/react";
import React from "react";
import { LinkButton } from "../atoms/link-button";
import { Logo } from "../atoms/logo";

export const SenderHeader: React.FC = () => {
  return (
    <Flex
      align="center"
      height="60px"
      py={2}
      px={4}
      borderBottom={1}
      borderStyle="solid"
      borderColor="gray.200"
    >
      <Flex flex={1} justify="flex-start">
        <Link to="/">
          <Logo />
        </Link>
        <Stack
          align="center"
          justify="flex-start"
          direction="row"
          spacing={4}
          ml={6}
        >
          <LinkButton to="/sender/send">請求書送付</LinkButton>
          <LinkButton to="/sender/invoices">請求書一覧</LinkButton>
        </Stack>
      </Flex>
      <Stack justify="flex-end" direction="row">
        <LinkButton to="/recipient">受領者に切り替える</LinkButton>
      </Stack>
    </Flex>
  );
};
