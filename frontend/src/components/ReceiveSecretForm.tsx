import React, { useState } from 'react';
import { useNats, NatsStatus } from '../hooks/useNats';

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
            setError('Both Message ID and Password (Key) are required.');
            return;
        }
        setIsLoading(true);
        setError(null);
        setDecryptedMessage(null);
        try {
            const message = await receiveSecret(id, password);
            setDecryptedMessage(message);
            setId(''); // Clear fields on success
            setPassword('');
        } catch (err) {
            setError((err as Error).message + "");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="bg-gray-800 p-6 sm:p-8 rounded-xl shadow-2xl">
            <h2 className="text-2xl sm:text-3xl font-semibold text-teal-300 mb-6 border-b border-gray-700 pb-4">
                Receive & Decrypt Secret
            </h2>
            <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                    <label htmlFor="messageId" className="block text-sm font-medium text-gray-300 mb-1">
                        Message ID:
                    </label>
                    <input
                        type="text"
                        id="messageId"
                        value={id}
                        onChange={(e) => setId(e.target.value)}
                        placeholder="Enter Message ID provided by sender"
                        required
                        className="w-full p-3 bg-gray-700 text-gray-200 border border-gray-600 rounded-lg focus:ring-2 focus:ring-teal-500 focus:border-teal-500 transition-colors duration-150 placeholder-gray-500"
                    />
                </div>
                <div>
                    <label htmlFor="password" className="block text-sm font-medium text-gray-300 mb-1">
                        Password (Key):
                    </label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Enter the unique Password (Key)"
                        required
                        className="w-full p-3 bg-gray-700 text-gray-200 border border-gray-600 rounded-lg focus:ring-2 focus:ring-teal-500 focus:border-teal-500 transition-colors duration-150 placeholder-gray-500"
                    />
                </div>
                <button
                    type="submit"
                    disabled={isLoading || natsStatus !== NatsStatus.CONNECTED}
                    className="w-full py-3 px-4 bg-teal-600 hover:bg-teal-700 text-white font-semibold rounded-lg shadow-md transition-all duration-150 ease-in-out disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center group"
                >
                    {isLoading ? (
                        <>
                            <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                            Decrypting Message...
                        </>
                    ) : (
                        <>
                            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2 group-hover:animate-keyframe-受信" viewBox="0 0 20 20" fill="currentColor">
                                <path d="M10 2a5 5 0 00-5 5v2a2 2 0 00-2 2v5a2 2 0 002 2h10a2 2 0 002-2v-5a2 2 0 00-2-2V7a5 5 0 00-5-5zm0 12.5a.5.5 0 010-1 .5.5 0 010 1zm0-8a2.5 2.5 0 00-2.5 2.5V9h5V6.5A2.5 2.5 0 0010 4z" />
                            </svg>
                            Fetch & Decrypt Secret
                        </>
                    )}
                </button>
            </form>
            {decryptedMessage && (
                <div className="mt-6 p-6 bg-gray-700 border border-gray-600 rounded-lg shadow-lg">
                    <strong className="block text-lg text-teal-300 mb-3">Decrypted Secret Message:</strong>
                    <pre className="whitespace-pre-wrap bg-gray-900 p-4 rounded text-gray-200 font-mono text-sm leading-relaxed shadow-inner">{decryptedMessage}</pre>
                    <p className="text-xs mt-4 text-yellow-400">This message has been self-destructed from the server.</p>
                </div>
            )}
            {error && (
                <div className="mt-6 p-4 bg-red-800 border border-red-700 rounded-lg text-red-100 shadow-lg">
                    <strong className="block">Decryption Failed:</strong> {error}
                </div>
            )}
        </div>
    );
};

export default ReceiveSecretForm;