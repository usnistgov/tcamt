package gov.nist.healthcare.tcamt.domain.report;

public class HL7V2MessageValidationReport {

	private HeaderReport headerReport;
	private SpecificReport specificReport;
	
	public HeaderReport getHeaderReport() {
		return headerReport;
	}
	public void setHeaderReport(HeaderReport headerReport) {
		this.headerReport = headerReport;
	}
	public SpecificReport getSpecificReport() {
		return specificReport;
	}
	public void setSpecificReport(SpecificReport specificReport) {
		this.specificReport = specificReport;
	}
}
