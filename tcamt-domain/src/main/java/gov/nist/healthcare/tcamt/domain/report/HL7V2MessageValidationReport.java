package gov.nist.healthcare.tcamt.domain.report;

import java.io.Serializable;

public class HL7V2MessageValidationReport implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7840304317044556623L;
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
