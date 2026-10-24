package exceptions;

public class FormatException extends Exception {
    private static final long serialVersionUID = 1L;

    private final int lineNumber;

    public FormatException(int lineNumber, String reason) {
        super("Строка " + lineNumber + ": " + reason);
        this.lineNumber = lineNumber;
    }

    public FormatException(int lineNumber, String reason, Throwable cause) {
        super("Строка " + lineNumber + ": " + reason, cause);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
