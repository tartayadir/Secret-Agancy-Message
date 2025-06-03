import { connect, NatsConnection, StringCodec, Subscription, Msg } from 'nats.ws';
import { useState, useEffect, useCallback } from 'react';

const NATS_URL = process.env.REACT_APP_NATS_URL || 'ws://localhost:9222'; // Example: 'ws://localhost:8080' if NATS has a WS listener on 8080

export enum NatsStatus {
    DISCONNECTED = 'DISCONNECTED',
    CONNECTING = 'CONNECTING',
    CONNECTED = 'CONNECTED',
}

export interface SaveSecretResponse {
    id: string;
    password?: string; // Corresponds to aesKey in diagram, user receives as "password"
}

export const parseEncryptedSecret = (str: string) : SaveSecretResponse => {
  let data: string[] = str.split(":");
  return {
      id: data[0],
      password: data[1]
  }
}

export function useNats() {
    const [natsConnection, setNatsConnection] = useState<NatsConnection | null>(null);
    const [natsStatus, setNatsStatus] = useState<NatsStatus>(NatsStatus.DISCONNECTED);
    const sc = StringCodec(); // For encoding/decoding NATS message payloads

    const connectToNats = useCallback(async () => {
        if (natsConnection && !natsConnection.isClosed()) {
            return;
        }
        setNatsStatus(NatsStatus.CONNECTING);
        try {
            console.log(`Attempting to connect to NATS at ${NATS_URL}`);
            const nc = await connect({
                servers: NATS_URL,
                reconnectTimeWait: 5000, // Wait 5 seconds between reconnect attempts
                maxReconnectAttempts: -1, // Keep trying to reconnect
            });
            setNatsConnection(nc);
            setNatsStatus(NatsStatus.CONNECTED);
            console.log('Successfully connected to NATS.');

            // Async iterator for status updates
            (async () => {
                for await (const status of nc.status()) {
                    console.info(`NATS status: ${status.type}`);
                    if (status.type === 'disconnect' || status.type === 'error') {
                        setNatsStatus(NatsStatus.DISCONNECTED);
                    }
                    if (status.type === 'reconnecting') {
                        setNatsStatus(NatsStatus.CONNECTING);
                    }
                    if (status.type === 'reconnect') {
                        setNatsStatus(NatsStatus.CONNECTED);
                    }
                }
            })().then();

        } catch (error) {
            console.error('Failed to connect to NATS:', error);
            setNatsStatus(NatsStatus.DISCONNECTED);
            setNatsConnection(null);
        }
    }, [natsConnection]);

    useEffect(() => {
        connectToNats();
        return () => {
            natsConnection?.close();
            console.log('NATS connection closed on component unmount.');
        };
    }, [connectToNats]); // connectToNats will not change, so this runs once on mount

    const saveSecret = async (secretMessage: string): Promise<SaveSecretResponse> => {
        if (!natsConnection || natsConnection.isClosed()) {
            throw new Error('NATS connection is not available.');
        }
        try {
            const reply = await natsConnection.request('save.msg', sc.encode(secretMessage), { timeout: 5000 });
            const decodedResponse = sc.decode(reply.data);
            // Assuming the backend sends a JSON string like {"id": "...", "password": "..."}
            // The diagram shows "aes key & id" as response [cite: 7]
            // "The recipient will receive the password and an id via a second secure factor." [cite: 2]
            return parseEncryptedSecret(decodedResponse) as SaveSecretResponse;
        } catch (err) {
            console.error('Error saving secret:', err);
            const natsError = err as { message?: string };
            if (natsError.message?.includes('TIMEOUT')) {
                throw new Error('Request to save secret timed out.');
            }
            // Attempt to parse error if it's a known structure, otherwise rethrow
            try {
                // Backend might send error as plain text or structured
                const errorResponse = sc.decode((err as Msg).data) || natsError.message || "Failed to save secret due to an unknown error.";
                if (errorResponse.startsWith("Error:")) {
                    throw new Error(errorResponse);
                }
                throw new Error(`Failed to save secret: ${errorResponse}`);
            } catch (parseOrOriginalError) {
                throw new Error(`Failed to save secret: ${natsError.message || 'Unknown error'}`);
            }
        }
    };

    const receiveSecret = async (id: string, passwordOrKey: string): Promise<string> => {
        if (!natsConnection || natsConnection.isClosed()) {
            throw new Error('NATS connection is not available.');
        }
        const payload = `${id}:${passwordOrKey}`;
        try {
            const reply = await natsConnection.request('receive.msg', sc.encode(payload), { timeout: 10000 }); // Longer timeout for potential decryption
            const decodedResponse = sc.decode(reply.data);
            // "If the recipient fails, the message is deleted." after 3 tries [cite: 5]
            // "As soon as the recipient successfully received the message, the message is deleted."
            if (decodedResponse.startsWith("Error:")) {
                throw new Error(decodedResponse);
            }
            return decodedResponse; // This is the decrypted message
        } catch (err) {
            console.error('Error receiving secret:', err);
            const natsError = err as { message?: string };
            if (natsError.message?.includes('TIMEOUT')) {
                throw new Error('Request to receive secret timed out.');
            }
            try {
                const errorResponse = sc.decode((err as Msg).data) || natsError.message || "Failed to receive secret due to an unknown error.";
                if (errorResponse.startsWith("Error:")) {
                    throw new Error(errorResponse);
                }
                throw new Error(`Failed to receive secret: ${errorResponse}`);
            } catch(parseOrOriginalError) {
                throw new Error(`Failed to receive secret: ${natsError.message || 'Unknown error'}`);
            }
        }
    };

    return { natsConnection, natsStatus, saveSecret, receiveSecret, connectToNats };
}