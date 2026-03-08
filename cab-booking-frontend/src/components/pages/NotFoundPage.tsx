import { Link } from 'react-router-dom';

const NotFoundPage = () => (
    <div className="flex flex-col items-center justify-center h-screen gap-4">
        <h1 className="text-4xl font-bold">404</h1>
        <p className="text-gray-600">This page doesn't exist.</p>
        <Link to="/" className="text-blue-600 underline">Go Home</Link>
    </div>
);

export default NotFoundPage;