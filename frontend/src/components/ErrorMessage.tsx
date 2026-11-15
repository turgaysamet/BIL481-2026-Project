import { ApiRequestError } from '../api/client';

/** Shows the message of a backend ApiError, or a generic text. */
export default function ErrorMessage({ error }: { error: unknown }) {
  if (!error) return null;
  const text = error instanceof ApiRequestError ? error.error.message : 'Something went wrong';
  return <p className="error" role="alert">{text}</p>;
}
