package gov.nist.healthcare.tcamt.web;

public class GeneralException extends Throwable {
    static final long serialVersionUID = -3387516993124221148L;

    public GeneralException() {
        super();
    }

    public GeneralException(String message) {
        super(message);
    }

    public GeneralException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneralException(Throwable cause) {
        super(cause);
    }

    protected GeneralException(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
