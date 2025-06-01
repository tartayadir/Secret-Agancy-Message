import React from 'react';
import { NatsStatus } from '../hooks/useNats';

interface NatConnectionStatusProps {
    status: NatsStatus;
    onReconnect: () => void;
}

const NatConnectionStatus: React.FC<NatConnectionStatusProps> = ({ status, onReconnect }) => {
    let statusText = 'Unknown';
    let statusClass = '';

    switch (status) {
        case NatsStatus.CONNECTED:
            statusText = 'Connected to NATS';
            statusClass = 'status-connected';
            break;
        case NatsStatus.CONNECTING:
            statusText = 'Connecting to NATS...';
            statusClass = 'status-connecting';
            break;
        case NatsStatus.DISCONNECTED:
            statusText = 'Disconnected from NATS';
            statusClass = 'status-disconnected';
            break;
    }

    return (
        <div className={`connection-status ${statusClass}`}>
            {statusText}
            {status === NatsStatus.DISCONNECTED && (
                <button onClick={onReconnect} style={{ marginLeft: '10px', padding: '5px 10px', fontSize: '14px' }}>
                    Reconnect
                </button>
            )}
        </div>
    );
};

export default NatConnectionStatus;