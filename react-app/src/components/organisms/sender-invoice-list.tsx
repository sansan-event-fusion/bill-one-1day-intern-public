import { Button, chakra, Table, Tbody, Td, Thead, Tr } from "@chakra-ui/react";
import React from "react";
import { useAdvancedExerciseContext } from "../../context/advanced-exercise-context";
import { SenderInvoice } from "../../domain/models/sender-invoice";
import { formatDateTime } from "../../util/formatter";

type SenderInvoiceListProps = {
  invoices: SenderInvoice[];
  onClickInvoice: (senderInvoice: SenderInvoice) => void;
};

const ThWithBorder = chakra(Td, {
  baseStyle: {
    border: "1px",
    borderColor: "gray.300",
  },
});

const TdWithBorder = chakra(Td, {
  baseStyle: {
    border: "1px",
    borderColor: "gray.300",
  },
});

export const SenderInvoiceList: React.FC<SenderInvoiceListProps> = ({
  invoices,
  onClickInvoice,
}) => {
  const { isAdvancedExerciseEnabled } = useAdvancedExerciseContext();

  return (
    <Table variant="simple">
      <Thead bg="gray.100">
        <Tr fontWeight="bold">
          <ThWithBorder />
          <ThWithBorder>登録日</ThWithBorder>
          <ThWithBorder>受領アカウント</ThWithBorder>
          <ThWithBorder>送付アカウント</ThWithBorder>
          {isAdvancedExerciseEnabled && <ThWithBorder>メモ</ThWithBorder>}
        </Tr>
      </Thead>
      <Tbody>
        {invoices.map((i) => (
          <Tr key={i.senderInvoiceUUID}>
            <TdWithBorder textAlign="center" width={4}>
              <Button
                variant="ghost"
                color="primary.button"
                onClick={() => onClickInvoice(i)}
              >
                詳細
              </Button>
            </TdWithBorder>
            <TdWithBorder>{formatDateTime(i.registeredAt)}</TdWithBorder>
            <TdWithBorder>{i.recipientFullName}</TdWithBorder>
            <TdWithBorder>{i.senderFullName}</TdWithBorder>
            {isAdvancedExerciseEnabled && <TdWithBorder>{i.memo}</TdWithBorder>}
          </Tr>
        ))}
      </Tbody>
    </Table>
  );
};
