interface Props { message?: string }

export function ErrorMessage({ message = 'Something went wrong.' }: Props) {
  return (
    <div className="rounded-md bg-red-50 border border-red-200 p-4 text-red-700 text-sm">
      {message}
    </div>
  );
}
