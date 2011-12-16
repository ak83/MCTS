package exceptions;

@SuppressWarnings("serial")
public class WhiteMoveFinderException extends Exception {

    public WhiteMoveFinderException() {}


    public WhiteMoveFinderException(String arg0) {
	super(arg0);
    }


    public WhiteMoveFinderException(Throwable arg0) {
	super(arg0);
    }


    public WhiteMoveFinderException(String arg0, Throwable arg1) {
	super(arg0, arg1);
    }

}
