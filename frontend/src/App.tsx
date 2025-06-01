import React from 'react';
import CreateSecretForm from './components/CreateSecretForm';
import ReceiveSecretForm from './components/ReceiveSecretForm';
import NatConnectionStatus from './components/NatConnectionStatus';
import { useNats } from './hooks/useNats'; // Import directly for App to use
import './styles.css';

function App() {
  // Initialize useNats here so status is available at App level
  // Components will also call useNats, React context behavior ensures they share the same instance if provider is used,
  // or they get their own instance. For this simple app, direct use is fine, or wrap with a Provider.
  // For this example, let's assume each major component section gets its own hook instance,
  // but status display will use one instance from App.
  const { natsStatus, connectToNats } = useNats();

  return (
      <div className="App">
        <h1>Self-Destructing Secret Agency Message Service [cite: 1]</h1>
        <NatConnectionStatus status={natsStatus} onReconnect={connectToNats} />
        <CreateSecretForm />
        <ReceiveSecretForm />
      </div>
  );
}

export default App;