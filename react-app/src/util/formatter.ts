import { format } from "date-fns";
import { ja } from "date-fns/locale";

export function formatDateTime(date: Date): string {
  return format(date, "PPpp", { locale: ja });
}
