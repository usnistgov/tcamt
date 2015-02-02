package gov.nist.healthcare.tcamt.web;

public class ConversionException extends Exception {

    private static final long serialVersionUID = 1L;

    public ConversionException(Exception e) {
	super(e);
    }

    public ConversionException(String e) {
	super(e);
    }

}
