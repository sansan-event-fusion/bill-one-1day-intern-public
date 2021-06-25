import { useEffect, useRef, useState } from "react";
import { fetchSenders } from "../../api/sender/sender";
import { Sender } from "../../domain/models/sender";

export const useSenders = () => {
  const [senders, setSenders] = useState<Sender[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  const unmounted = useRef(false);

  useEffect(() => {
    setLoading(true);
    const fn = async () => {
      const senders = await fetchSenders().catch((error) => {
        setError(error);
        setLoading(false);
        return null;
      });
      if (unmounted.current || senders === null) return;

      setSenders(senders);
      setLoading(false);
    };
    fn();

    return () => {
      unmounted.current = true;
    };
  }, []);

  return { senders, loading, error };
};
