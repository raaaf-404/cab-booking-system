// pages/Homepage.tsx
import React from 'react';
import { TaraLogo } from '@/components/ui/TaraLogo.tsx';
import { MapPin, Navigation, ShieldCheck, Clock } from 'lucide-react';

const Homepage = () => {
    return (
        <div className="min-h-screen bg-slate-50 text-slate-900 font-sans selection:bg-blue-200">

            {/* 1. NAVBAR - Keep it clean and accessible */}
            <header className="px-6 py-4 flex items-center justify-between bg-white/80 backdrop-blur-md sticky top-0 z-50 shadow-sm">
                <TaraLogo />
                <nav className="hidden md:flex gap-6 font-medium text-slate-600">
                    <a href="#ride" className="hover:text-blue-600 transition-colors">Ride</a>
                    <a href="#drive" className="hover:text-blue-600 transition-colors">Drive</a>
                    <a href="#about" className="hover:text-blue-600 transition-colors">About Us</a>
                </nav>
                <div className="flex gap-4">
                    <button className="hidden md:block px-4 py-2 font-medium text-slate-700 hover:text-blue-600 transition-colors">Log In</button>
                    <button className="px-5 py-2 bg-blue-600 text-white rounded-full font-medium hover:bg-blue-700 transition-colors shadow-md hover:shadow-lg">
                        Sign Up
                    </button>
                </div>
            </header>

            {/* 2. HERO SECTION - Focus on the primary user action (Booking) */}
            <main>
                <section className="relative px-6 py-16 md:py-24 max-w-7xl mx-auto flex flex-col md:flex-row items-center gap-12">

                    {/* Left Column: Copy & Booking Form Placeholder */}
                    <div className="flex-1 w-full z-10">
                        <h1 className="text-5xl md:text-6xl font-extrabold tracking-tight text-slate-900 mb-6 leading-tight">
                            Get there with <span className="text-blue-600">Tara.</span>
                        </h1>
                        <p className="text-lg text-slate-600 mb-8 max-w-md">
                            Fast, reliable, and safe rides at your fingertips. Request a ride, hop in, and go.
                        </p>

                        {/* Mockup of the Booking Form Component */}
                        <div className="bg-white p-6 rounded-3xl shadow-[0_8px_30px_rgb(0,0,0,0.08)] border border-slate-100 max-w-md">
                            <div className="flex flex-col gap-4">
                                <div className="relative">
                                    <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 w-5 h-5" />
                                    <input
                                        type="text"
                                        placeholder="Enter pickup location"
                                        className="w-full bg-slate-100 text-slate-900 px-12 py-4 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                                    />
                                </div>
                                <div className="relative">
                                    <Navigation className="absolute left-4 top-1/2 -translate-y-1/2 text-blue-500 w-5 h-5" />
                                    <input
                                        type="text"
                                        placeholder="Where to?"
                                        className="w-full bg-slate-100 text-slate-900 px-12 py-4 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                                    />
                                </div>
                                <button className="w-full mt-2 bg-slate-900 text-white py-4 rounded-xl font-semibold text-lg hover:bg-slate-800 transition-colors">
                                    See prices
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* Right Column: Abstract Illustration / Aesthetic Element */}
                    <div className="flex-1 w-full relative hidden md:block">
                        {/* Note: In a real app, replace this div with an actual SVG illustration from unDraw or your designer */}
                        <div className="relative w-full aspect-square max-w-lg mx-auto bg-gradient-to-tr from-blue-100 to-slate-50 rounded-full flex items-center justify-center shadow-inner overflow-hidden">
                            <div className="absolute inset-0 opacity-20" style={{ backgroundImage: 'radial-gradient(#3B82F6 2px, transparent 2px)', backgroundSize: '30px 30px'}}></div>
                            <img
                                src="https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?auto=format&fit=crop&q=80&w=800"
                                alt="A modern cab on a city street"
                                className="w-3/4 h-3/4 object-cover rounded-3xl shadow-2xl rotate-[-5deg] hover:rotate-0 transition-transform duration-500"
                            />
                        </div>
                    </div>
                </section>

                {/* 3. FEATURES SECTION - Highlighting value proposition */}
                <section className="bg-white py-20 px-6">
                    <div className="max-w-7xl mx-auto">
                        <h2 className="text-3xl font-bold text-center mb-16 text-slate-900">Why ride with Tara?</h2>

                        <div className="grid md:grid-cols-3 gap-10">
                            <FeatureCard
                                icon={<Clock className="w-8 h-8 text-blue-600" />}
                                title="Always on time"
                                description="Our smart routing ensures your driver takes the fastest, most efficient route to your destination."
                            />
                            <FeatureCard
                                icon={<ShieldCheck className="w-8 h-8 text-blue-600" />}
                                title="Safety first"
                                description="Every driver is background-checked. Share your trip status with loved ones for peace of mind."
                            />
                            <FeatureCard
                                icon={<MapPin className="w-8 h-8 text-blue-600" />}
                                title="Everywhere you are"
                                description="Available in over 500+ cities worldwide. Wherever you need to go, we're already there."
                            />
                        </div>
                    </div>
                </section>
            </main>
        </div>
    );
};

// Sub-component for clean mapping.
// Note: If this gets complex, move it to its own file!
const FeatureCard = ({ icon, title, description }: { icon: React.ReactNode, title: string, description: string }) => (
    <div className="flex flex-col items-center text-center p-6 rounded-2xl hover:bg-slate-50 transition-colors">
        <div className="w-16 h-16 bg-blue-50 rounded-full flex items-center justify-center mb-6">
            {icon}
        </div>
        <h3 className="text-xl font-bold text-slate-900 mb-3">{title}</h3>
        <p className="text-slate-600 leading-relaxed">{description}</p>
    </div>
);

export default Homepage;