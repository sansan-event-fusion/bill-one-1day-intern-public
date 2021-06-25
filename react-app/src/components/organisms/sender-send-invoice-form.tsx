import { SmallCloseIcon } from "@chakra-ui/icons";
import {
  Box,
  Button,
  FormControl,
  FormLabel,
  IconButton,
  Select,
  Stack,
  Textarea,
} from "@chakra-ui/react";
import React, { useCallback, useState } from "react";
import { useAdvancedExerciseContext } from "../../context/advanced-exercise-context";
import { Recipient } from "../../domain/models/recipient";
import { Sender } from "../../domain/models/sender";
import { Dropzone } from "../atoms/dropzone";

type SenderSendInvoiceFormProps = {
  recipients: Recipient[];
  senders: Sender[];
  onChangeInvoiceFile: (file: File | null) => void;
  onSubmit: (
    recipientUUID: string,
    senderUUID: string,
    invoice: File,
    memo?: string
  ) => Promise<void>;
};

export const SenderSendInvoiceForm: React.FC<SenderSendInvoiceFormProps> = ({
  recipients,
  senders,
  onChangeInvoiceFile,
  onSubmit,
}) => {
  const { isAdvancedExerciseEnabled } = useAdvancedExerciseContext();

  const [recipientUUID, setRecipientUUID] = useState("");
  const [senderUUID, setSenderUUID] = useState("");
  const [memo, setMemo] = useState("");
  const [invoiceFile, setInvoiceFile] = useState<File | null>(null);

  const handleChangeInvoiceFile = useCallback(
    (files: File[] | null) => {
      const file = files && files[0];

      setInvoiceFile(file);
      onChangeInvoiceFile(file);
    },
    [onChangeInvoiceFile]
  );

  const resetFormValue = useCallback(() => {
    setInvoiceFile(null);
    onChangeInvoiceFile(null);
    setMemo("");
  }, [onChangeInvoiceFile]);

  const handleSubmit: React.FormEventHandler = useCallback(
    async (e) => {
      e.preventDefault();
      if (recipientUUID === "" || senderUUID === "") return;
      if (invoiceFile === null)
        return window.alert("ファイルを選択してください");

      const memoValue = isAdvancedExerciseEnabled ? memo : undefined;
      await onSubmit(recipientUUID, senderUUID, invoiceFile, memoValue)
        .then(() => resetFormValue())
        .catch((error) => console.error(error));
    },
    [
      invoiceFile,
      isAdvancedExerciseEnabled,
      memo,
      onSubmit,
      recipientUUID,
      resetFormValue,
      senderUUID,
    ]
  );

  return (
    <Stack as="form" spacing={4} onSubmit={handleSubmit}>
      <FormControl id="recipient" isRequired>
        <FormLabel htmlFor="recipient">受領者を選択</FormLabel>
        <Select
          id="recipient"
          placeholder="選択してください"
          onChange={(e) => setRecipientUUID(e.target.value)}
        >
          {recipients.map((r) => (
            <option key={r.recipientUUID} value={r.recipientUUID}>
              {r.fullName}
            </option>
          ))}
        </Select>
      </FormControl>
      <FormControl id="sender" isRequired>
        <FormLabel>送付者を選択</FormLabel>
        <Select
          id="sender"
          placeholder="選択してください"
          onChange={(e) => setSenderUUID(e.target.value)}
        >
          {senders.map((s) => (
            <option key={s.senderUUID} value={s.senderUUID}>
              {s.fullName}
            </option>
          ))}
        </Select>
      </FormControl>
      <Box>
        {invoiceFile && (
          <Box
            backgroundColor="gray.50"
            py={2}
            pl={4}
            borderWidth="medium"
            borderBottom="none"
            borderColor="gray.200"
            boxSizing="border-box"
          >
            {invoiceFile.name}
            <IconButton
              aria-label="ファイルを削除する"
              variant="unstyled"
              icon={<SmallCloseIcon />}
              ml={2}
              onClick={() => handleChangeInvoiceFile(null)}
            />
          </Box>
        )}
        <Dropzone onChange={handleChangeInvoiceFile} />
      </Box>
      {isAdvancedExerciseEnabled && (
        <Box>
          <FormControl id="memo">
            <Textarea
              placeholder="メモを記入してください"
              value={memo}
              onChange={(e) => setMemo(e.target.value)}
            />
          </FormControl>
        </Box>
      )}
      <Box pt={2}>
        <Button
          type="submit"
          colorScheme="blue"
          backgroundColor="primary.button"
          color="white"
          px={5}
          size="md"
        >
          送付
        </Button>
      </Box>
    </Stack>
  );
};
