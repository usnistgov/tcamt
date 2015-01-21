package gov.nist.healthcare.tcamt.domain;

import java.io.Serializable;

public enum TestDataCategorization implements Serializable {
	Indifferent("Indifferent"),
	ContentIndifferent("Content Indifferent"), Configurable("Configurable"), SystemGenerated("System Generated"), TestCaseProper("Test Case Proper"), ProfileFixedPresence("Profile Fixed Presence"),
	NotValued("Not-Valued"),
	ProfileFixed("Profile Fixed"), ProfileFixedList("Profile Fixed List"), TestCaseFixed("Test Case Fixed"), TestCaseFixedList("Test Case Fixed List"),
	TestCaseFixedLength("Test Case Fixed Length");

	private String value;

	TestDataCategorization(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
