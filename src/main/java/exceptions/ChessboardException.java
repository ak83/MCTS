package exceptions;

@SuppressWarnings("serial")
public class ChessboardException extends Exception {

    public ChessboardException(String message) {
        super(message);
    }


    public ChessboardException() {
        super();
    }

}
