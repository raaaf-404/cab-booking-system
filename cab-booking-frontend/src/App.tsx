import { RouterProvider } from 'react-router-dom';
import { router } from '@/routes';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

// Create a client for React Query
const queryClient = new QueryClient();

function App() {
  return (
    // Provide the query client to your entire app
    <QueryClientProvider client={queryClient}>
      
      {/* This component will render your routes */}
      <RouterProvider router={router} />

    </QueryClientProvider>
  );
}

export default App;