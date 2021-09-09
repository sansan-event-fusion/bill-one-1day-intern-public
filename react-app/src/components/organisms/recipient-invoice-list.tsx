import { Button, chakra, Table, Tbody, Td, Thead, Tr } from "@chakra-ui/react";
import React from "react";
import { useAdvancedExerciseContext } from "../../context/advanced-exercise-context";
import { Invoice } from "../../domain/models/invoice";
import { formatDateTime } from "../../util/formatter";

type RecipientInvoiceListProps = {
  invoices: Invoice[];
  onClickInvoice: (senderInvoice: Invoice) => void;
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

export const RecipientInvoiceList: React.FC<RecipientInvoiceListProps> = ({
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
          <ThWithBorder>送付アカウント</ThWithBorder>
          <ThWithBorder>受領アカウント</ThWithBorder>
          {isAdvancedExerciseEnabled && <ThWithBorder>メモ</ThWithBorder>}
        </Tr>
      </Thead>
      <Tbody>
        {invoices.map((i) => (
          <Tr key={i.invoiceUUID}>
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
            <TdWithBorder>{i.senderFullName}</TdWithBorder>
            <TdWithBorder>{i.recipientFullName}</TdWithBorder>
            {isAdvancedExerciseEnabled && <TdWithBorder>{i.memo}</TdWithBorder>}
          </Tr>
        ))}
      </Tbody>
    </Table>
  );
};
