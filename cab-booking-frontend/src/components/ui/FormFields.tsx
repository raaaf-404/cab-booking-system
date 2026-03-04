import { ReactNode } from 'react';

interface FormFieldProps {
    label: string;
    htmlFor?: string;
    error?: string;
    description?: string;
    children: ReactNode;
    className?: string; // Allow custom styling for the wrapper
}

export const FormField = ({
                              label,
                              htmlFor,
                              error,
                              description,
                              children,
                              className = ""
                          }: FormFieldProps) => {
    return (
        <div className={`flex flex-col gap-1.5 ${className}`}>
            <label
                htmlFor={htmlFor}
                className="text-sm font-semibold text-gray-700 leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
            >
                {label}
            </label>

            {/* Render the input (Input, Select, etc.) */}
            {children}

            {/* Helper Text (only if no error, or always visible - depends on design) */}
            {description && !error && (
                <p className="text-[0.8rem] text-gray-500 text-muted-foreground">
                    {description}
                </p>
            )}

            {/* Error Message */}
            {error && (
                <p className="text-sm font-medium text-red-500 animate-in slide-in-from-top-1 fade-in-0">
                    {error}
                </p>
            )}
        </div>
    );
};

