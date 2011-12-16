package exceptions;

@SuppressWarnings("serial")
public class MandatoryConfigFileParamaterMissingException extends Exception {

    public MandatoryConfigFileParamaterMissingException() {}


    public MandatoryConfigFileParamaterMissingException(String arg0) {
        super(arg0);
    }


    public MandatoryConfigFileParamaterMissingException(Throwable arg0) {
        super(arg0);
    }


    public MandatoryConfigFileParamaterMissingException(String arg0,
            Throwable arg1) {
        super(arg0, arg1);
    }

}
