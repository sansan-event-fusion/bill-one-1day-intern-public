import { Center, Spinner } from "@chakra-ui/react";
import React from "react";

export const Loader: React.FC = () => {
  return (
    <Center
      position="absolute"
      top={0}
      left={0}
      right={0}
      bottom={0}
      zIndex={10}
    >
      <Spinner
        thickness="4px"
        speed="0.65s"
        emptyColor="gray.200"
        color="primary.text"
        size="xl"
      />
    </Center>
  );
};
