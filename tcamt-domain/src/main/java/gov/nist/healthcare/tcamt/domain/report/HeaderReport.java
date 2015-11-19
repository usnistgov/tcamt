package gov.nist.healthcare.tcamt.domain.report;

import java.io.Serializable;

public class HeaderReport implements Serializable {
/**
	 * 
	 */
	private static final long serialVersionUID = 68108141431964724L;
/*
 		<ns5:ServiceName>Unified Report Test Application</ns5:ServiceName>
        <ns5:ServiceProvider>NIST</ns5:ServiceProvider>
        <ns5:ServiceVersion>1.0</ns5:ServiceVersion>
        <ns5:StandardType>HL7 V2</ns5:StandardType>
        <ns5:ValidationType>Automated</ns5:ValidationType>
        <ns5:Type>Context-Free</ns5:Type>
        <ns5:DateOfTest>2015-09-21-04:00</ns5:DateOfTest>
        <ns5:TimeOfTest>11:01:54.429-04:00</ns5:TimeOfTest>
        <ns5:PositiveAssertionIndicator>false</ns5:PositiveAssertionIndicator>
        <ns5:AffirmCount>0</ns5:AffirmCount>
        <ns5:ErrorCount>0</ns5:ErrorCount>
        <ns5:WarningCount>0</ns5:WarningCount>
        <ns5:InfoCount>36</ns5:InfoCount>
        <ns5:AlertCount>0</ns5:AlertCount>
 */
	
	
	private String serviceName;
	private String serviceProvider;
	private String serviceVersion;
	private String standardType;
	private String validationType;
	private String type;
	private String dateOfTest;
	private String timeOfTest;
	private String positiveAssertionIndicator;
	private String affirmCount;
	private String errorCount;
	private String warningCount;
	private String infoCount;
	private String alertCount;
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceProvider() {
		return serviceProvider;
	}
	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
	public String getServiceVersion() {
		return serviceVersion;
	}
	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}
	public String getStandardType() {
		return standardType;
	}
	public void setStandardType(String standardType) {
		this.standardType = standardType;
	}
	public String getValidationType() {
		return validationType;
	}
	public void setValidationType(String validationType) {
		this.validationType = validationType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDateOfTest() {
		return dateOfTest;
	}
	public void setDateOfTest(String dateOfTest) {
		this.dateOfTest = dateOfTest;
	}
	public String getTimeOfTest() {
		return timeOfTest;
	}
	public void setTimeOfTest(String timeOfTest) {
		this.timeOfTest = timeOfTest;
	}
	public String getPositiveAssertionIndicator() {
		return positiveAssertionIndicator;
	}
	public void setPositiveAssertionIndicator(String positiveAssertionIndicator) {
		this.positiveAssertionIndicator = positiveAssertionIndicator;
	}
	public String getAffirmCount() {
		return affirmCount;
	}
	public void setAffirmCount(String affirmCount) {
		this.affirmCount = affirmCount;
	}
	public String getErrorCount() {
		return errorCount;
	}
	public void setErrorCount(String errorCount) {
		this.errorCount = errorCount;
	}
	public String getWarningCount() {
		return warningCount;
	}
	public void setWarningCount(String warningCount) {
		this.warningCount = warningCount;
	}
	public String getInfoCount() {
		return infoCount;
	}
	public void setInfoCount(String infoCount) {
		this.infoCount = infoCount;
	}
	public String getAlertCount() {
		return alertCount;
	}
	public void setAlertCount(String alertCount) {
		this.alertCount = alertCount;
	}
	
	
}
