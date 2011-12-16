package exceptions;

@SuppressWarnings("serial")
public class WhiteMoveChooserException extends Exception {

    public WhiteMoveChooserException() {}


    public WhiteMoveChooserException(String arg0) {
	super(arg0);
    }


    public WhiteMoveChooserException(Throwable arg0) {
	super(arg0);
    }


    public WhiteMoveChooserException(String arg0, Throwable arg1) {
	super(arg0, arg1);
    }

}
