import React, { useState } from 'react';
import { useNats, SaveSecretResponse } from '../hooks/useNats';

const CreateSecretForm: React.FC = () => {
    const { saveSecret, natsStatus } = useNats();
    const [secretMessage, setSecretMessage] = useState<string>('');
    const [result, setResult] = useState<SaveSecretResponse | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        if (!secretMessage.trim()) {
            setError('Secret message cannot be empty.');
            return;
        }
        setIsLoading(true);
        setError(null);
        setResult(null);
        console.log(secretMessage)
        try {
            const response = await saveSecret(secretMessage);
            setResult(response);
        } catch (err) {
            setError((err as Error).message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="container">
            <h2>Create Self-Destructing Secret</h2>
            <form onSubmit={handleSubmit} className="form-section">
                <label htmlFor="secretMessage">Your Secret Message:</label>
                <textarea
                    id="secretMessage"
                    value={secretMessage}
                    onChange={(e) => setSecretMessage(e.target.value)}
                    placeholder="Enter your secret message here..."
                    rows={4}
                    required
                />
                <button type="submit" disabled={isLoading || natsStatus !== 'CONNECTED'}>
                    {isLoading ? 'Creating...' : 'Create Secret'}
                </button>
            </form>
            {result && (
                <div className="result-box">
                    <strong>Secret Created Successfully!</strong>
                    Share these details with the recipient via a secure channel: [cite: 2, 3]
                    <p>ID: <code>{result.id}</code></p>
                    {/* The backend generates the password and it cannot be set by the creator [cite: 2] */}
                    {/* The password is not saved in the crypto service [cite: 2] */}
                    <p>Password: <code>{result.password}</code></p>
                </div>
            )}
            {error && <div className="error-box">{error}</div>}
        </div>
    );
};

export default CreateSecretForm;