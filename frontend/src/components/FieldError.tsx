import "./FieldError.css";

export default function FieldError({ message }: Readonly<{ message?: string }>) {
  if (!message) return null;
  return (
    <div className="field-error">
      <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
              d="M12 9v2m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
      </svg>
      {message}
    </div>
  );
}
