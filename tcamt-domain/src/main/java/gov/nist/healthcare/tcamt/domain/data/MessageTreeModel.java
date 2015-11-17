package gov.nist.healthcare.tcamt.domain.data;

public class MessageTreeModel implements Cloneable{

	private Object node;
	private String messageId;
	private String path;
	private String name;
	protected int occurrence;

	public MessageTreeModel(String messageId, String name, Object node, String path, int occurrence) {
		super();
		this.messageId = messageId;
		this.node = node;
		this.name = name;
		this.path = path;
		this.occurrence = occurrence;
	}

	public Object getNode() {
		return node;
	}

	public void setNode(Object node) {
		this.node = node;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(int occurrence) {
		this.occurrence = occurrence;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
