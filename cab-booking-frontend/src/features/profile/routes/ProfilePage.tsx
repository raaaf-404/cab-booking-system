import { useQuery } from '@tanstack/react-query';
import { useAuthStore } from '@/store/useAuthStore';
import { getMyBookings } from '@/api/bookingApi';

const ProfilePage = () => {

  // 1. Get the user from our global store (Client State)
  const { user } = useAuthStore();

// 2. Fetch bookings from the API (Server State)
const {
    data: bookings,
    isLoading,
    error,
  } = useQuery({
    queryKey: ['my-bookings'], // Unique key for caching
    queryFn: getMyBookings,    // The function we just created
  });

return (
    <div className="mx-auto max-w-4xl p-6">
      {/* Header Section */}
      <div className="mb-8 rounded-lg bg-white p-6 shadow-sm">
        <h1 className="text-3xl font-bold text-gray-800">My Profile</h1>
        <div className="mt-4 text-gray-600">
          <p>
            <span className="font-semibold">Username:</span> {user?.username}
          </p>
          <p>
            <span className="font-semibold">Email:</span> {user?.email}
          </p>
          <p>
            <span className="font-semibold">Role:</span>{' '}
            {user?.roles.join(', ')}
          </p>
        </div>
      </div>

      {/* Bookings Section */}
      <h2 className="mb-4 text-2xl font-bold text-gray-800">My Ride History</h2>

      {/* Loading State */}
      {isLoading && <p className="text-gray-500">Loading your rides...</p>}

      {/* Error State */}
      {error && (
        <p className="text-red-500">
          Error loading history: {error.message}
        </p>
      )}

      {/* Empty State */}
      {!isLoading && bookings?.length === 0 && (
        <p className="text-gray-500">You haven't booked any rides yet.</p>
      )}

      {/* Bookings List */}
      <div className="flex flex-col gap-4">
        {bookings?.map((booking) => (
          <div
            key={booking.bookingId}
            className="flex flex-col justify-between rounded-lg border bg-white p-4 shadow-sm sm:flex-row sm:items-center"
          >
            <div>
              <p className="font-semibold text-lg text-blue-600">
                {booking.pickupLocation} ➝ {booking.dropoffLocation}
              </p>
              <p className="text-sm text-gray-500">
                {new Date(booking.pickupTime).toLocaleString()}
              </p>
              <p className="mt-1 text-sm text-gray-600">
                Cab: {booking.cab.licensePlate} ({booking.cab.cabType})
              </p>
            </div>
            <div className="mt-2 text-right sm:mt-0">
              <span
                className={`inline-block rounded px-2 py-1 text-xs font-semibold uppercase ${
                  booking.bookingStatus === 'COMPLETED'
                    ? 'bg-green-100 text-green-800'
                    : booking.bookingStatus === 'CANCELLED'
                    ? 'bg-red-100 text-red-800'
                    : 'bg-yellow-100 text-yellow-800'
                }`}
              >
                {booking.bookingStatus}
              </span>
              <p className="mt-1 font-bold text-gray-800">
                ${booking.fare.toFixed(2)}
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ProfilePage;