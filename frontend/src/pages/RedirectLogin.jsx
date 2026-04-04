import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

/**
 * Smart redirect component to handle legacy or QR-style links like:
 * /login?hotelId=1&tableId=2
 * and send them to the correct path-based route:
 * /1/login?tableId=2
 */
const RedirectLogin = () => {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const queryParams = new URLSearchParams(location.search);
        const hotelId = queryParams.get('hotelId');
        const tableId = queryParams.get('tableId');

        if (hotelId) {
            // Found hotelId in query params, redirect to proper path
            let target = `/${hotelId}/login`;
            if (tableId) {
                target += `?tableId=${tableId}`;
            }
            navigate(target, { replace: true });
        } else {
            // No hotelId found, fallback to home
            navigate('/', { replace: true });
        }
    }, [navigate, location]);

    return (
        <div className="min-h-screen bg-[#0D0D0D] flex items-center justify-center">
            <div className="flex flex-col items-center gap-4">
                <div className="w-12 h-12 border-4 border-amber-500 border-t-transparent rounded-full animate-spin"></div>
                <p className="text-gray-400 font-bold uppercase tracking-widest text-xs">Redirecting to your table...</p>
            </div>
        </div>
    );
};

export default RedirectLogin;
