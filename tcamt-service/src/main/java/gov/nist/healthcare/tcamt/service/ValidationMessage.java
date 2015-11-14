package gov.nist.healthcare.tcamt.service;

public class ValidationMessage {

	public void er7MessageValidation(String er7Message, String profile, String constraints, String valueSets, String oid) throws Exception {
//		ValidationProxy vp = new ValidationProxy("TCAMT Instant Testing", "NIST");
//		
//		InputStream csis = IOUtils.toInputStream(constraints, "UTF-8");
//		List<InputStream> confContexts = Arrays.asList(csis);
//		ConformanceContext conformanceContext = DefaultConformanceContext.apply(confContexts).get();
//		
//		InputStream vsLibXMLis = IOUtils.toInputStream(valueSets, "UTF-8");
//		ValueSetLibrary valueSetLibrary = ValueSetLibraryImpl.apply(vsLibXMLis).get();
//		
//		EnhancedReport report = vp.validate(er7Message, profile, conformanceContext, valueSetLibrary, oid, Context.Free);
//		
//		String resultXML = report.to("xml").toString();
//		
//		
//		
//		System.out.println(resultXML);
//		
//		Document resultDom = XMLManager.stringToDom(resultXML);
//		HL7V2MessageValidationReport hL7V2MessageValidationReport = new HL7V2MessageValidationReport();
//		
//		HeaderReport headerReport = new HeaderReport();
//		
//		
//		Element headerReportElm = (Element)resultDom.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message/hl7/v2/report","HeaderReport").item(0);
//		
//		headerReport.setAffirmCount(headerReportElm.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message","AffirmCount").item(0).getChildNodes().item(0).getNodeValue());
//		headerReport.setAlertCount(headerReportElm.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message","AlertCount").item(0).getChildNodes().item(0).getNodeValue());
//		headerReport.setDateOfTest(headerReportElm.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message","DateOfTest").item(0).getChildNodes().item(0).getNodeValue());
//		headerReport.setErrorCount(headerReportElm.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message","ErrorCount").item(0).getChildNodes().item(0).getNodeValue());
//		headerReport.setInfoCount(headerReportElm.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message","InfoCount").item(0).getChildNodes().item(0).getNodeValue());
//		headerReport.setPositiveAssertionIndicator(headerReportElm.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message","PositiveAssertionIndicator").item(0).getChildNodes().item(0).getNodeValue());
//		headerReport.setServiceName(headerReportElm.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message","ServiceName").item(0).getChildNodes().item(0).getNodeValue());
//		headerReport.setServiceProvider(headerReportElm.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message","ServiceProvider").item(0).getChildNodes().item(0).getNodeValue());
//		headerReport.setServiceVersion(headerReportElm.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message","ServiceVersion").item(0).getChildNodes().item(0).getNodeValue());
//		headerReport.setStandardType(headerReportElm.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message","StandardType").item(0).getChildNodes().item(0).getNodeValue());
//		headerReport.setTimeOfTest(headerReportElm.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message","TimeOfTest").item(0).getChildNodes().item(0).getNodeValue());
//		headerReport.setType(headerReportElm.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message","Type").item(0).getChildNodes().item(0).getNodeValue());
//		headerReport.setValidationType(headerReportElm.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message","ValidationType").item(0).getChildNodes().item(0).getNodeValue());
//		headerReport.setWarningCount(headerReportElm.getElementsByTagNameNS("http://www.nist.gov/healthcare/validation/message","WarningCount").item(0).getChildNodes().item(0).getNodeValue());
//		
//		hL7V2MessageValidationReport.setHeaderReport(headerReport);
//		
//		return hL7V2MessageValidationReport;
		
	}
}
