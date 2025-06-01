import React, { useState } from 'react';
import { useNats } from '../hooks/useNats';

const ReceiveSecretForm: React.FC = () => {
    const { receiveSecret, natsStatus } = useNats();
    const [id, setId] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [decryptedMessage, setDecryptedMessage] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        if (!id.trim() || !password.trim()) {
            setError('Both ID and Password are required.');
            return;
        }
        setIsLoading(true);
        setError(null);
        setDecryptedMessage(null);
        try {
            // "The recipient has three tries to enter the right password." [cite: 2, 5]
            // This logic is handled by the backend.
            const message = await receiveSecret(id, password);
            // "As soon as the recipient successfully received the message, the message is deleted."
            setDecryptedMessage(message);
        } catch (err) {
            setError((err as Error).message);
            // "If the recipient fails, the message is deleted." (after 3 tries) [cite: 5]
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="container">
            <h2>Receive Secret Message</h2>
            <form onSubmit={handleSubmit} className="form-section">
                <div>
                    <label htmlFor="messageId">Message ID:</label>
                    <input
                        type="text"
                        id="messageId"
                        value={id}
                        onChange={(e) => setId(e.target.value)}
                        placeholder="Enter Message ID"
                        required
                    />
                </div>
                <div>
                    <label htmlFor="password">Password (Key):</label>
                    <input
                        type="password" // Use password type for better UX, though it's an AES key
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Enter Password (Key)"
                        required
                    />
                </div>
                <button type="submit" disabled={isLoading || natsStatus !== 'CONNECTED'}>
                    {isLoading ? 'Fetching...' : 'Fetch Secret'}
                </button>
            </form>
            {decryptedMessage && (
                <div className="result-box">
                    <strong>Secret Message:</strong>
                    <p>{decryptedMessage}</p>
                </div>
            )}
            {error && <div className="error-box">{error}</div>}
        </div>
    );
};

export default ReceiveSecretForm;