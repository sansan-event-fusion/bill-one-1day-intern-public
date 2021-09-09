import { ChakraProvider, extendTheme } from "@chakra-ui/react";
import React from "react";

const theme = extendTheme({
  colors: {
    primary: {
      button: "#2185d0",
      text: "#4183c4",
    },
  },
});

export const ThemeProvider: React.VFC<{ children: React.ReactChild }> = ({
  children,
}) => {
  return <ChakraProvider theme={theme}>{children}</ChakraProvider>;
};
