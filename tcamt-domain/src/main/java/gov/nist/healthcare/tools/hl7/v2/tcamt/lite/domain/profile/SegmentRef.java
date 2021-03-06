package gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile;

import org.bson.types.ObjectId;

public class SegmentRef extends SegmentRefOrGroup implements Cloneable {

	private static final long serialVersionUID = 1L;

	public SegmentRef() {
		super();
		type = Constant.SEGMENTREF;
		this.id = ObjectId.get().toString();
	}

	// @JsonIgnoreProperties({ "label", "fields", "dynamicMappings", "name",
	// "description", "predicates", "conformanceStatements", "comment",
	// "usageNote", "type", "text1", "text2" })
	private String ref;

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
		// this.refId = ref != null ? ref.getId() : null;
	}

	// public String getRefId() {
	// return refId;
	// }
	//
	// public void setRefId(String refId) {
	// this.refId = refId;
	// }

	@Override
	public String toString() {
		return "SegmentRef [segment=" + ref + ", usage=" + usage + ", min="
				+ min + ", max=" + max + "]";
	}

	@Override
	public SegmentRef clone() throws CloneNotSupportedException {
		SegmentRef clonedSegmentRef = new SegmentRef();
		clonedSegmentRef.setComment(comment);
		clonedSegmentRef.setMax(max);
		clonedSegmentRef.setMin(min);
		clonedSegmentRef.setPosition(position);
		clonedSegmentRef.setRef(ref);
		clonedSegmentRef.setUsage(usage);

		return clonedSegmentRef;
	}

}
