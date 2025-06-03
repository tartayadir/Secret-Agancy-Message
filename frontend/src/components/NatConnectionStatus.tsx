import React from 'react';
import { NatsStatus } from '../hooks/useNats';

interface NatConnectionStatusProps {
    status: NatsStatus;
    onReconnect: () => void;
}

const NatConnectionStatus: React.FC<NatConnectionStatusProps> = ({ status, onReconnect }) => {
    let statusText = 'Unknown NATS Connection';
    let bgColor = 'bg-gray-700';
    let textColor = 'text-gray-300';
    let borderColor = 'border-gray-600';
    let showReconnectButton = false;

    switch (status) {
        case NatsStatus.CONNECTED:
            statusText = 'Secure Channel: Connected to NATS';
            bgColor = 'bg-green-700';
            textColor = 'text-green-100';
            borderColor = 'border-green-600';
            break;
        case NatsStatus.CONNECTING:
            statusText = 'Establishing Secure Channel: Connecting to NATS...';
            bgColor = 'bg-yellow-600';
            textColor = 'text-yellow-100';
            borderColor = 'border-yellow-500';
            break;
        case NatsStatus.DISCONNECTED:
            statusText = 'Secure Channel: Disconnected from NATS';
            bgColor = 'bg-red-700';
            textColor = 'text-red-100';
            borderColor = 'border-red-600';
            showReconnectButton = true;
            break;
    }

    return (
        <div className={`p-4 mb-8 rounded-lg shadow-md border-l-4 ${borderColor} ${bgColor} ${textColor} flex flex-col sm:flex-row justify-between items-center`}>
            <p className="font-semibold">{statusText}</p>
            {showReconnectButton && (
                <button
                    onClick={onReconnect}
                    className="mt-2 sm:mt-0 sm:ml-4 px-4 py-2 bg-indigo-500 hover:bg-indigo-600 text-white font-medium rounded-md transition-colors duration-150 focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:ring-opacity-75"
                >
                    Reconnect
                </button>
            )}
        </div>
    );
};

export default NatConnectionStatus;