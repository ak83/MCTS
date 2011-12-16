package exceptions;

@SuppressWarnings("serial")
public class BlackMoveFinderException extends Exception {

    public BlackMoveFinderException() {}


    public BlackMoveFinderException(String arg0) {
	super(arg0);
    }


    public BlackMoveFinderException(Throwable arg0) {
	super(arg0);
    }


    public BlackMoveFinderException(String arg0, Throwable arg1) {
	super(arg0, arg1);
    }

}
