import { Flex, Stack } from "@chakra-ui/react";
import React from "react";
import { Link } from "react-router-dom";
import { LinkButton } from "../atoms/link-button";
import { Logo } from "../atoms/logo";

export const RecipientHeader: React.FC = () => {
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
      <Flex flex={{ base: 1 }} justify={{ base: "center", md: "start" }}>
        <Link to="/">
          <Logo />
        </Link>
      </Flex>
      <Stack flex={1} justify="flex-end" direction="row">
        <LinkButton to="/sender">送付者に切り替える</LinkButton>
      </Stack>
    </Flex>
  );
};
