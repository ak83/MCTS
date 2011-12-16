package exceptions;

@SuppressWarnings("serial")
public class MCTUtilsException extends Exception {

    public MCTUtilsException() {}


    public MCTUtilsException(String arg0) {
	super(arg0);
    }


    public MCTUtilsException(Throwable arg0) {
	super(arg0);
    }


    public MCTUtilsException(String arg0, Throwable arg1) {
	super(arg0, arg1);
    }

}
