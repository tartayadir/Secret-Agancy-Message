import React from 'react';

const Footer: React.FC = () => {
    const currentYear = new Date().getFullYear();
    return (
        <footer className="bg-gray-800 text-gray-400 text-center p-6 mt-auto">
            <p>&copy; {currentYear} CipherSafe Agency. All rights reserved.</p>
            <p className="text-xs mt-1">Messages are end-to-end encrypted and self-destruct upon reading.</p>
        </footer>
    );
};

export default Footer;