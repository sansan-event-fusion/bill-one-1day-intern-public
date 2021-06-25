import { Center } from "@chakra-ui/react";
import React from "react";

type CardProps = {
  children: React.ReactChild;
};
export const Card: React.VFC<CardProps> = ({ children }) => {
  return (
    <Center bg="white" border="1px" borderColor="gray.200" borderRadius="md">
      {children}
    </Center>
  );
};
