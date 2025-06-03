import React, { useState } from 'react';
import { useNats, SaveSecretResponse, NatsStatus } from '../hooks/useNats';

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
        try {
            const response = await saveSecret(secretMessage);
            setResult(response);
            setSecretMessage(''); // Clear message on success
        } catch (err) {
            setError((err as Error).message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="bg-gray-800 p-6 sm:p-8 rounded-xl shadow-2xl mb-10">
            <h2 className="text-2xl sm:text-3xl font-semibold text-indigo-300 mb-6 border-b border-gray-700 pb-4">
                Create Self-Destructing Secret
            </h2>
            <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                    <label htmlFor="secretMessage" className="block text-sm font-medium text-gray-300 mb-1">
                        Your Secret Message:
                    </label>
                    <textarea
                        id="secretMessage"
                        value={secretMessage}
                        onChange={(e) => setSecretMessage(e.target.value)}
                        placeholder="Type your confidential message here..."
                        rows={5}
                        required
                        className="w-full p-3 bg-gray-700 text-gray-200 border border-gray-600 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors duration-150 placeholder-gray-500"
                    />
                </div>
                <button
                    type="submit"
                    disabled={isLoading || natsStatus !== NatsStatus.CONNECTED}
                    className="w-full py-3 px-4 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold rounded-lg shadow-md transition-all duration-150 ease-in-out disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center group"
                >
                    {isLoading ? (
                        <>
                            <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                            Creating Secure Channel...
                        </>
                    ) : (
                        <>
                            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2 group-hover:animate-pulse" viewBox="0 0 20 20" fill="currentColor">
                                <path fillRule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clipRule="evenodd" />
                            </svg>
                            Create Secret & Get Credentials
                        </>
                    )}
                </button>
            </form>
            {result && (
                <div className="mt-6 p-4 bg-green-800 border border-green-700 rounded-lg text-green-100 shadow-lg">
                    <strong className="block text-lg mb-2">Secret Credentials Generated!</strong>
                    <p className="text-sm mb-2">Share these details with the recipient via a secure channel:</p>
                    <div className="space-y-1">
                        <p className="font-mono text-sm">ID: <code className="bg-gray-700 px-2 py-1 rounded text-indigo-300">{result.id}</code></p>
                        <p className="font-mono text-sm">Password: <code className="bg-gray-700 px-2 py-1 rounded text-indigo-300">{result.password}</code></p>
                    </div>
                    <p className="text-xs mt-3 text-green-300">This password is not stored and cannot be recovered once this message is dismissed.</p>
                </div>
            )}
            {error && (
                <div className="mt-6 p-4 bg-red-800 border border-red-700 rounded-lg text-red-100 shadow-lg">
                    <strong className="block">Error:</strong> {error}
                </div>
            )}
        </div>
    );
};

export default CreateSecretForm;