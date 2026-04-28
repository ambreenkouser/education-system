import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useGetMeQuery, useUpdateUserMutation } from './userApi';
import { useAuth } from '@/hooks/useAuth';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { ErrorMessage } from '@/components/common/ErrorMessage';

const schema = z.object({
  firstName: z.string().min(1, 'Required'),
  lastName:  z.string().min(1, 'Required'),
  phone:     z.string().optional(),
  address:   z.string().optional(),
});
type FormData = z.infer<typeof schema>;

export function ProfilePage() {
  const { userId } = useAuth();
  const { data: profile, isLoading, error } = useGetMeQuery();
  const [updateUser, { isLoading: saving, isSuccess }] = useUpdateUserMutation();

  const { register, handleSubmit, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
    values: profile ? { firstName: profile.firstName, lastName: profile.lastName, phone: profile.phone ?? '', address: profile.address ?? '' } : undefined,
  });

  if (isLoading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message="Could not load profile." />;

  async function onSubmit(data: FormData) {
    if (!userId) return;
    await updateUser({ userId, body: data }).unwrap();
  }

  return (
    <div className="max-w-lg">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">My Profile</h1>
      {isSuccess && <div className="mb-4 text-sm text-green-700 bg-green-50 border border-green-200 rounded p-3">Profile updated.</div>}
      <div className="bg-white rounded-xl shadow-sm p-6">
        <div className="mb-4">
          <span className="text-xs font-semibold uppercase tracking-wider text-gray-400">Email</span>
          <p className="text-gray-800 mt-1">{profile?.email}</p>
        </div>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {(['firstName', 'lastName', 'phone', 'address'] as const).map((field) => (
            <div key={field}>
              <label className="block text-sm font-medium text-gray-700 mb-1 capitalize">{field.replace(/([A-Z])/g, ' $1')}</label>
              <input
                {...register(field)}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              {errors[field] && <p className="text-xs text-red-500 mt-1">{errors[field]?.message}</p>}
            </div>
          ))}
          <button
            type="submit"
            disabled={saving}
            className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium disabled:opacity-60"
          >
            {saving ? 'Saving…' : 'Save changes'}
          </button>
        </form>
      </div>
    </div>
  );
}
