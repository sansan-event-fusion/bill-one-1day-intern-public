import { Button } from "@chakra-ui/react";
import React from "react";
import { NavLink } from "react-router-dom";

type LinkButtonProps = {
  children: React.ReactChild;
  to: `/${string}`;
};

export const LinkButton: React.VFC<LinkButtonProps> = ({ children, to }) => {
  return (
    <Button
      fontSize="sm"
      colorScheme="grey"
      variant="link"
      as={NavLink}
      to={to}
    >
      {children}
    </Button>
  );
};
