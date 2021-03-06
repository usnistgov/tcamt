package gov.nist.healthcare.tools.hl7.v2.igamt.lite.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DataElement extends DataModel implements java.io.Serializable, Cloneable,
    Comparable<DataElement> {

  private static final long serialVersionUID = 1L;

  // @Id
  // //@Column(name = "ID")
  // //@GeneratedValue(strategy = GenerationType.TABLE)
  // protected String id;

  // //@NotNull
  // //@Column(nullable = false, name = "DATAELEMENT_NAME")
  protected String name;

  // //@NotNull
  // //@Column(name = "USAGEE", nullable = false)
  // // usage is a key word in mysql
  // @Enumerated(EnumType.STRING)
  protected Usage usage;
  //
  // @Min(0)
  // //@Column(name = "MIN_LENGTH")
  protected Integer minLength;

  // //@NotNull
  // //@Column(nullable = false, name = "MAX_LENGTH")
  protected String maxLength;

  // //@Column(name = "CONF_LENGTH")
  protected String confLength;

  // @JsonIgnoreProperties({ "mappingAlternateId", "mappingId", "name",
  // "version", "codesys", "oid", "tableType", "stability",
  // "extensibility", "type", "codes" })
  // //@ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = {
  // CascadeType.PERSIST, CascadeType.MERGE })
  // //@JoinColumn(name = "TABLE_ID")
  protected TableLink table;
  
  protected List<TableLink> tables = new ArrayList<TableLink>();


  // //@JsonIgnore

  // @JsonIgnoreProperties({ "label", "components", "name", "description",
  // "predicates", "conformanceStatements", "comment", "usageNote",
  // "type" })
  protected DatatypeLink datatype;

  // protected String datatypeId;

  // //@NotNull
  // //@Column(nullable = false, name = "DATAELEMENT_POSITION")
  protected Integer position = 0;

  // //@Column(name = "COMMENT")
  protected String comment = "";

  protected String text = "";

  protected boolean hide;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public DatatypeLink getDatatype() {
    return datatype;
  }

  public void setDatatype(DatatypeLink datatype) {
    this.datatype = datatype;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Usage getUsage() {
    return usage;
  }

  public void setUsage(Usage usage) {
    this.usage = usage;
  }

  public Integer getMinLength() {
    return minLength;
  }

  public void setMinLength(Integer minLength) {
    this.minLength = minLength;
  }

  public String getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(String maxLength) {
    this.maxLength = maxLength;
  }

  public String getConfLength() {
    return confLength;
  }

  public void setConfLength(String confLength) {
    this.confLength = confLength;
  }

//  @Deprecated
//  public TableLink getTable() {
//    return table;
//  }
//
//  @Deprecated 
//  public void setTable(TableLink table) {
//    this.table = table;
//  }


  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  // public String getDatatypeLabel() {
  // return datatypeLabel;
  // }
  //
  // // DO NO SET
  // public void setDatatypeLabel(String datatypeLabel) {
  // this.datatypeLabel = datatypeLabel;
  // }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  // public String getTableId() {
  // return tableId;
  // }
  //
  // public void setTableId(String tableId) {
  // this.tableId = tableId;
  // }

  public boolean isHide() {
    return hide;
  }

  public void setHide(boolean hide) {
    this.hide = hide;
  }

  public List<TableLink> getTables() {
	return tables;
  }

  public void setTables(List<TableLink> tables) {
	this.tables = tables;
  }

@Override
  protected DataElement clone() throws CloneNotSupportedException {
    DataElement de = (DataElement) super.clone();
    List<TableLink> links = new ArrayList<TableLink>();
    de.setTables(links);
    Collections.copy(links,this.tables);
    // de.setDatatype(this.datatype.clone());
    de.setDatatype(this.datatype.clone()); // Changed by Harold

    return de;
  }

  public int compareTo(DataElement o) {
    return this.getPosition() - o.getPosition();
  }

}
