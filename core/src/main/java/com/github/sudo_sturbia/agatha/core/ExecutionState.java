package com.github.sudo_sturbia.agatha.core;

/**
 * ExecutionState It is used as a server response indicating state
 * of execution of request. It can be used as an error code.
 * <p>
 * Execution code is one of the following.
 * <pre>
 *     0. Operation performed correctly.
 *     1. Wrong syntax/structure.
 *     2. Incorrect credentials.
 *     3. Operation failed.
 *     4. Account locked for invalid login attempts.
 * </pre>
 * All codes are accompanied by a message.
 */
public class ExecutionState
{
    /** Execution code. */
    private final int code;

    /** An error message. Null if no errors occurred. */
    private final String message;

    /**
     * Create an ExecutionState object with given code.
     *
     * @param code execution code.
     * @throws IllegalArgumentException if code is invalid.
     */
    public ExecutionState(int code) throws IllegalArgumentException
    {
        switch (code)
        {
            case 0:
                this.message = "Operation performed successfully.";
                break;
            case 1:
                this.message = "Wrong request syntax/structure.";
                break;
            case 2:
                this.message = "Incorrect login credentials.";
                break;
            case 3:
                this.message = "Operation failed.";
                break;
            case 4:
                this.message = "Account locked for invalid login attempts.";
                break;
            case 5:
                this.message = "Uses invalid characters in username/book's name.";
                break;
            default:
                throw new IllegalArgumentException("Invalid execution code.");
        }

        this.code = code;
    }

    /**
     * Get execution code.
     *
     * @return execution code.
     */
    public int getCode() {
        return this.code;
    }

    /**
     * Get error message.
     *
     * @return error message.
     */
    public String getMessage() {
        return this.message;
    }
}
