import React from 'react';

const Header: React.FC = () => {
    return (
        <header className="bg-gray-800 shadow-lg">
            <div className="container mx-auto px-6 py-4">
                <h1 className="text-3xl font-bold text-white tracking-tight">
                    <span className="text-indigo-400">Cipher</span>Safe Agency
                </h1>
                <p className="text-sm text-gray-400">Self-Destructing Message Service</p>
            </div>
        </header>
    );
};

export default Header;