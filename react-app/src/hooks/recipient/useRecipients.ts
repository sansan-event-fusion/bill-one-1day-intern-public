import { useEffect, useRef, useState } from "react";
import { fetchRecipients } from "../../api/recipient/recipient";
import { Recipient } from "../../domain/models/recipient";

export const useRecipients = () => {
  const [recipients, setRecipients] = useState<Recipient[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  const unmounted = useRef(false);

  useEffect(() => {
    setLoading(true);
    const fn = async () => {
      const recipients = await fetchRecipients().catch((error) => {
        setError(error);
        setLoading(false);
        return null;
      });
      if (unmounted.current || recipients === null) return;

      setRecipients(recipients);
      setLoading(false);
    };
    fn();

    return () => {
      unmounted.current = true;
    };
  }, []);

  return { recipients, loading, error };
};
