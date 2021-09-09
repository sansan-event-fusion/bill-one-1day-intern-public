import { Center } from "@chakra-ui/react";
import React from "react";
import { DropzoneProps, useDropzone } from "react-dropzone";

export type CustomDropzoneProps = Omit<DropzoneProps, "onDrop"> & {
  onChange: (files: File[]) => void;
};

export const Dropzone: React.FC<CustomDropzoneProps> = ({
  onChange,
  ...props
}) => {
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop: onChange,
    ...props,
  });

  return (
    <Center
      height="250px"
      direction="column"
      p={5}
      backgroundColor="gray.100"
      borderWidth="medium"
      borderColor={isDragActive ? "primary.text" : "gray.200"}
      color="gray.700"
      outline="none"
      cursor="pointer"
      transition="border .24s ease-in-out"
      {...getRootProps()}
    >
      <input {...getInputProps()} />
      {isDragActive ? (
        <p>ここにドロップ...</p>
      ) : (
        <p>
          ここにファイルをドラッグ＆ドロップ または クリックしてファイルを選択
        </p>
      )}
    </Center>
  );
};
