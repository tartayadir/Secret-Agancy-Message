import React from 'react';
import Header from './components/Header';
import Footer from './components/Footer';
import CreateSecretForm from './components/CreateSecretForm';
import ReceiveSecretForm from './components/ReceiveSecretForm';
import NatConnectionStatus from './components/NatConnectionStatus';
import { useNats, NatsStatus } from './hooks/useNats';

function App() {
    const { natsStatus, connectToNats } = useNats();

    return (
        <div className="flex flex-col min-h-screen bg-gray-900 text-gray-100">
            <Header />

            <main className="flex-grow container mx-auto px-4 py-8 sm:px-6 lg:px-8">

                <NatConnectionStatus status={natsStatus} onReconnect={connectToNats} />

                <div className="grid grid-cols-1 lg:grid-cols-2 lg:gap-12">
                    <CreateSecretForm />
                    <ReceiveSecretForm />
                </div>
            </main>

            <Footer />
        </div>
    );
}

export default App;