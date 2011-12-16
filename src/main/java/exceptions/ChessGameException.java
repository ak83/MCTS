package exceptions;

@SuppressWarnings("serial")
public class ChessGameException extends Exception {

    public ChessGameException() {}


    public ChessGameException(String arg0) {
	super(arg0);
    }


    public ChessGameException(Throwable arg0) {
	super(arg0);
    }


    public ChessGameException(String arg0, Throwable arg1) {
	super(arg0, arg1);
    }

}
