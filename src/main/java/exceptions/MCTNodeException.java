package exceptions;

@SuppressWarnings("serial")
public class MCTNodeException extends Exception {

    public MCTNodeException() {
	super();
    }


    public MCTNodeException(String message) {
	super(message);
    }


    public MCTNodeException(Throwable cause) {
	super(cause);
    }


    public MCTNodeException(String message, Throwable cause) {
	super(message, cause);
    }

}
