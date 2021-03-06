package gov.nist.healthcare.tcamt.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This software was developed at the National Institute of Standards and Technology by employees
 * of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
 * United States Code this software is not subject to copyright protection and is in the public domain.
 * This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
 * and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
 * We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
 * modified freely provided that any derivative works bear some notice that they are derived from it, and any
 * modified versions bear some notice that they have been modified.
 */

import gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Code;
import gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.ContentDefinition;
import gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Extensibility;
import gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Profile;
import gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Stability;
import gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Table;
import gov.nist.healthcare.tools.hl7.v2.tcamt.lite.domain.profile.Tables;
import nu.xom.Attribute;

public class TableSerializationImpl implements TableSerialization {
        public Tables deserializeXMLToTableLibrary(String xmlContents) {
                Document tableLibraryDoc = this.stringToDom(xmlContents);
                Tables tableLibrary = new Tables();
                Element elmTableLibrary = (Element) tableLibraryDoc.getElementsByTagName("ValueSetLibrary").item(0);
                tableLibrary.setValueSetLibraryIdentifier(elmTableLibrary.getAttribute("ValueSetLibraryIdentifier"));
                this.deserializeXMLToTable(elmTableLibrary, tableLibrary);

                return tableLibrary;
        }
        public Tables deserializeXMLToTableLibrary(nu.xom.Document xmlDoc) {
                return deserializeXMLToTableLibrary(xmlDoc.toString());
        }
        public String serializeTableLibraryToXML(Tables tableLibrary) {
        	return this.serializeTableLibraryToDoc(tableLibrary).toXML();
        }
        public nu.xom.Document serializeTableLibraryToDoc(Tables tableLibrary) {
        	Profile p = new Profile();
        	p.setTables(tableLibrary);
        	p.setMetaData(null);
        	return this.serializeTableLibraryToDoc(p);
        }
		public String serializeTableLibraryToXML(Profile profile) {
        	return this.serializeTableLibraryToDoc(profile).toXML();
		}
		public String serializeTableLibraryToGazelleXML(Profile profile) {
        	return this.serializeTableLibraryToGazelleDoc(profile).toXML();
		}
        
        
        public nu.xom.Element serializeTableLibraryToGazelleElement(Profile profile) {
        	Tables tableLibrary = profile.getTables();
        	
        	nu.xom.Element elmSpecification = new nu.xom.Element("Specification");
        	
//        	if(profile.getMetaData() == null){
        		elmSpecification.addAttribute(new Attribute("SpecName", "NOSpecName"));
        		elmSpecification.addAttribute(new Attribute("OrgName", "NIST"));
        		elmSpecification.addAttribute(new Attribute("HL7Version", "1"));
        		elmSpecification.addAttribute(new Attribute("SpecVersion", "1"));
        		elmSpecification.addAttribute(new Attribute("Status", "Draft"));
        		elmSpecification.addAttribute(new Attribute("ConformanceType", "Tolerant"));
        		elmSpecification.addAttribute(new Attribute("Role", "Sender"));
        		elmSpecification.addAttribute(new Attribute("HL7OID", ""));
        		elmSpecification.addAttribute(new Attribute("ProcRule", "HL7"));
//        	}else {
//        		elmSpecification.addAttribute(new Attribute("SpecName", ExportUtil.str(profile.getMetaData().getSpecificationName())));
//        		elmSpecification.addAttribute(new Attribute("OrgName", ExportUtil.str(profile.getMetaData().getOrgName())));
//        		elmSpecification.addAttribute(new Attribute("HL7Version", ExportUtil.str(profile.getMetaData().getHl7Version())));
//        		elmSpecification.addAttribute(new Attribute("SpecVersion", ExportUtil.str(profile.getMetaData().getVersion())));
//        		elmSpecification.addAttribute(new Attribute("Status", ExportUtil.str(profile.getMetaData().getStatus())));
//        		elmSpecification.addAttribute(new Attribute("ConformanceType", "Tolerant"));
//        		elmSpecification.addAttribute(new Attribute("Role", "Sender"));
//        		elmSpecification.addAttribute(new Attribute("HL7OID", ""));
//        		elmSpecification.addAttribute(new Attribute("ProcRule", "HL7"));
//        	}
        	
        	nu.xom.Element elmConformance = new nu.xom.Element("Conformance");
        	elmConformance.addAttribute(new Attribute("AccAck", "NE"));
        	elmConformance.addAttribute(new Attribute("AppAck", "AL"));
        	elmConformance.addAttribute(new Attribute("StaticID", ""));
        	elmConformance.addAttribute(new Attribute("MsgAckMode", "Deferred"));
        	elmConformance.addAttribute(new Attribute("QueryStatus", "Event"));
        	elmConformance.addAttribute(new Attribute("QueryMode", "Non Query"));
        	elmConformance.addAttribute(new Attribute("DynamicID", ""));
        	elmSpecification.appendChild(elmConformance);
        	
        	nu.xom.Element elmEncodings = new nu.xom.Element("Encodings");
        	nu.xom.Element elmEncoding = new nu.xom.Element("Encoding");
        	elmEncoding.appendChild("ER7");
        	elmEncodings.appendChild(elmEncoding);
        	elmSpecification.appendChild(elmEncodings);
        	
        	int tableID = 0;
        	nu.xom.Element elmHl7tables = new nu.xom.Element("hl7tables");
        	
        	for (Table t : tableLibrary.getChildren()) {
        		tableID = tableID + 1;
        		nu.xom.Element elmHl7table = new nu.xom.Element("hl7table");
        		elmHl7table.addAttribute(new Attribute("id", tableID + ""));
        		elmHl7table.addAttribute(new Attribute("name", ExportUtil.str(t.getBindingIdentifier())));
        		elmHl7table.addAttribute(new Attribute("type", "HL7"));
        		
        		int order = 0;
        		List<String> codesysList = new ArrayList<String>();
        		
        		for (Code c : t.getCodes()) {
        			order = order + 1;
        			if(c.getCodeSystem() != null && !codesysList.contains(c.getCodeSystem())) codesysList.add(c.getCodeSystem());
        			
        			nu.xom.Element elmTableElement = new nu.xom.Element("tableElement");
        			elmTableElement.addAttribute(new Attribute("order", order + ""));
        			elmTableElement.addAttribute(new Attribute("code", ExportUtil.str(c.getValue())));
        			elmTableElement.addAttribute(new Attribute("description", ExportUtil.str(c.getLabel())));
        			elmTableElement.addAttribute(new Attribute("displayName", ExportUtil.str(c.getLabel())));
        			
        			if(c.getCodeSystem() == null || c.getCodeSystem().equals("")) elmTableElement.addAttribute(new Attribute("source", "NOSource"));
        			else elmTableElement.addAttribute(new Attribute("source", ExportUtil.str(c.getCodeSystem())));
        			elmTableElement.addAttribute(new Attribute("usage", "Optional"));
        			elmTableElement.addAttribute(new Attribute("creator", ""));
        			elmTableElement.addAttribute(new Attribute("date", ""));
        			elmTableElement.addAttribute(new Attribute("instruction", ""));
        			elmHl7table.appendChild(elmTableElement);
        		}
        		
        		if(codesysList.size() == 0) {
        			elmHl7table.addAttribute(new Attribute("codeSys", ""));
        		}else {
        			String codeSysSet = "";
        			for(String s:codesysList){
        				codeSysSet = codeSysSet + "," + s;
        			}
        			elmHl7table.addAttribute(new Attribute("codeSys", codeSysSet.substring(1)));
        		}
        		
        		elmHl7tables.appendChild(elmHl7table);
        	}
        	
        	elmSpecification.appendChild(elmHl7tables);
        	
        	return elmSpecification;
        }
        
        public nu.xom.Element serializeTableLibraryToElement(Profile profile) {
        	Tables tableLibrary = profile.getTables();
			
			nu.xom.Element elmTableLibrary = new nu.xom.Element("ValueSetLibrary");
			
			if(tableLibrary.getValueSetLibraryIdentifier() == null || tableLibrary.getValueSetLibraryIdentifier().equals("")){
				elmTableLibrary.addAttribute(new Attribute("ValueSetLibraryIdentifier",UUID.randomUUID().toString()));
			}else {
				elmTableLibrary.addAttribute(new Attribute("ValueSetLibraryIdentifier",ExportUtil.str(tableLibrary.getValueSetLibraryIdentifier())));
			}
            nu.xom.Element elmMetaData = new nu.xom.Element("MetaData");
            if(profile.getMetaData() == null){
            	elmMetaData.addAttribute(new Attribute("Name", "Vocab for " + "Profile"));
                elmMetaData.addAttribute(new Attribute("OrgName", "NIST"));
                elmMetaData.addAttribute(new Attribute("Version", "1.0.0"));
                elmMetaData.addAttribute(new Attribute("Date", ""));
            }else {
            	elmMetaData.addAttribute(new Attribute("Name", "Vocab for " + profile.getMetaData().getName()));
                elmMetaData.addAttribute(new Attribute("OrgName", ExportUtil.str(profile.getMetaData().getOrgName())));
                elmMetaData.addAttribute(new Attribute("Version", ExportUtil.str(profile.getMetaData().getVersion())));
                elmMetaData.addAttribute(new Attribute("Date", ExportUtil.str(profile.getMetaData().getDate())));
                
                if(profile.getMetaData().getSpecificationName() != null && !profile.getMetaData().getSpecificationName().equals("")) elmMetaData.addAttribute(new Attribute("SpecificationName", ExportUtil.str(profile.getMetaData().getSpecificationName())));
                if(profile.getMetaData().getStatus() != null && !profile.getMetaData().getStatus().equals("")) elmMetaData.addAttribute(new Attribute("Status", ExportUtil.str(profile.getMetaData().getStatus())));
                if(profile.getMetaData().getTopics() != null && !profile.getMetaData().getTopics().equals("")) elmMetaData.addAttribute(new Attribute("Topics", ExportUtil.str(profile.getMetaData().getTopics())));
            }

            HashMap<String, nu.xom.Element> valueSetDefinitionsMap = new HashMap<String, nu.xom.Element>();

            for (Table t : tableLibrary.getChildren()) {
            	nu.xom.Element elmValueSetDefinition = new nu.xom.Element("ValueSetDefinition");
                elmValueSetDefinition.addAttribute(new Attribute("BindingIdentifier", ExportUtil.str(t.getBindingIdentifier())));
                elmValueSetDefinition.addAttribute(new Attribute("Name", ExportUtil.str(t.getName())));
                if(t.getDescription() != null && !t.getDescription().equals("")) elmValueSetDefinition.addAttribute(new Attribute("Description",ExportUtil.str( t.getDescription())));
                if(t.getVersion() != null && !t.getVersion().equals("")) elmValueSetDefinition.addAttribute(new Attribute("Version", ExportUtil.str(t.getVersion())));
                if(t.getOid() != null && !t.getOid().equals("")) elmValueSetDefinition.addAttribute(new Attribute("Oid",ExportUtil.str(t.getOid())));
                if(t.getStability() != null && !t.getStability().equals("")) elmValueSetDefinition.addAttribute(new Attribute("Stability", ExportUtil.str(t.getStability().value())));
                if(t.getExtensibility() != null && !t.getExtensibility().equals("")) elmValueSetDefinition.addAttribute(new Attribute("Extensibility", ExportUtil.str(t.getExtensibility().value())));
                if(t.getContentDefinition() != null && !t.getContentDefinition().equals("")) elmValueSetDefinition.addAttribute(new Attribute("ContentDefinition", ExportUtil.str(t.getContentDefinition().value())));
                
                
                nu.xom.Element elmValueSetDefinitions = null;
                if(t.getGroup() != null && !t.getGroup().equals("")){
                	elmValueSetDefinitions = valueSetDefinitionsMap.get(t.getGroup());
                }else {
                	elmValueSetDefinitions = valueSetDefinitionsMap.get("NOGroup");
                }
                if(elmValueSetDefinitions == null) {
                	elmValueSetDefinitions = new nu.xom.Element("ValueSetDefinitions");
                	
                	if(t.getGroup() != null && !t.getGroup().equals("")){
                		elmValueSetDefinitions.addAttribute(new Attribute("Group", t.getGroup()));
                    	elmValueSetDefinitions.addAttribute(new Attribute("Order", t.getOrder() + ""));
                    	valueSetDefinitionsMap.put(t.getGroup(), elmValueSetDefinitions);
                	}else {
                		elmValueSetDefinitions.addAttribute(new Attribute("Group", "NOGroup"));
                    	elmValueSetDefinitions.addAttribute(new Attribute("Order", "0"));
                    	valueSetDefinitionsMap.put("NOGroup", elmValueSetDefinitions);
                	}
                	
                }
                elmValueSetDefinitions.appendChild(elmValueSetDefinition);

                if (t.getCodes() != null) {
                	for (Code c : t.getCodes()) {
                        nu.xom.Element elmValueElement = new nu.xom.Element("ValueElement");
                        elmValueElement.addAttribute(new Attribute("Value", ExportUtil.str(c.getValue())));
                        elmValueElement.addAttribute(new Attribute("DisplayName", ExportUtil.str(c.getLabel() + "")));
                        if(c.getCodeSystem() != null && !c.getCodeSystem().equals("")) elmValueElement.addAttribute(new Attribute("CodeSystem", ExportUtil.str(c.getCodeSystem())));
                        if(c.getCodeSystemVersion() != null && !c.getCodeSystemVersion().equals("")) elmValueElement.addAttribute(new Attribute("CodeSystemVersion", ExportUtil.str(c.getCodeSystemVersion())));
                        if(c.getCodeUsage() != null && !c.getCodeUsage().equals("")) elmValueElement.addAttribute(new Attribute("Usage", ExportUtil.str(c.getCodeUsage())));
                        if(c.getComments() != null && !c.getComments().equals("")) elmValueElement.addAttribute(new Attribute("Comments", ExportUtil.str(c.getComments())));
                        elmValueSetDefinition.appendChild(elmValueElement);
                	}
                }
            }
            
            elmTableLibrary.appendChild(elmMetaData);
            
            for(nu.xom.Element elmValueSetDefinitions:valueSetDefinitionsMap.values()){
            	elmTableLibrary.appendChild(elmValueSetDefinitions);
            }
            
            return elmTableLibrary;
        }
		public nu.xom.Document serializeTableLibraryToDoc(Profile profile) {
            return new nu.xom.Document(this.serializeTableLibraryToElement(profile));
		}
		
		public nu.xom.Document serializeTableLibraryToGazelleDoc(Profile profile) {
            return new nu.xom.Document(this.serializeTableLibraryToGazelleElement(profile));
		}

        private void deserializeXMLToTable(Element elmTableLibrary, Tables tableLibrary) {
        	 	NodeList valueSetDefinitionsNode = elmTableLibrary.getElementsByTagName("ValueSetDefinitions");
        	 	for (int i = 0; i < valueSetDefinitionsNode.getLength(); i++) {
        	 		Element valueSetDefinitionsElement = (Element) valueSetDefinitionsNode.item(i);
        	 		NodeList valueSetDefinitionNodes = valueSetDefinitionsElement.getElementsByTagName("ValueSetDefinition");
                    for (int j = 0; j < valueSetDefinitionNodes.getLength(); j++) {
                            Element elmTable = (Element) valueSetDefinitionNodes.item(j);

                            Table tableObj = new Table();

                            tableObj.setBindingIdentifier(elmTable.getAttribute("BindingIdentifier"));
                            tableObj.setName(elmTable.getAttribute("Name"));
                            
                            tableObj.setGroup(valueSetDefinitionsElement.getAttribute("Group"));
                            String orderStr = valueSetDefinitionsElement.getAttribute("Order");
                            if(orderStr != null && !orderStr.equals("")){
                            	tableObj.setOrder(Integer.parseInt(orderStr));
                	 		}

                            if (elmTable.getAttribute("Description") != null && !elmTable.getAttribute("Description").equals("")) tableObj.setDescription(elmTable.getAttribute("Description"));
                            if (elmTable.getAttribute("Version") != null && !elmTable.getAttribute("Version").equals("")) tableObj.setVersion(elmTable.getAttribute("Version"));
                            if (elmTable.getAttribute("Oid") != null && !elmTable.getAttribute("Oid").equals("")) tableObj.setOid(elmTable.getAttribute("Oid"));

                            if (elmTable.getAttribute("Extensibility") != null && !elmTable.getAttribute("Extensibility").equals("")) {
                                    tableObj.setExtensibility(Extensibility.fromValue(elmTable.getAttribute("Extensibility")));
                            } else {
                                    tableObj.setExtensibility(Extensibility.fromValue("Open"));
                            }

                            if (elmTable.getAttribute("Stability") != null && !elmTable.getAttribute("Stability").equals("")) {
                                    tableObj.setStability(Stability.fromValue(elmTable.getAttribute("Stability")));
                            } else {
                                    tableObj.setStability(Stability.fromValue("Static"));
                            }

                            if (elmTable.getAttribute("ContentDefinition") != null && !elmTable.getAttribute("ContentDefinition").equals("")) {
                                    tableObj.setContentDefinition(ContentDefinition.fromValue(elmTable.getAttribute("ContentDefinition")));
                            } else {
                                    tableObj.setContentDefinition(ContentDefinition.fromValue("Extensional"));
                            }

                            this.deserializeXMLToCode(elmTable, tableObj);
                            tableLibrary.addTable(tableObj);
                    }
        	 	}
        }

        private void deserializeXMLToCode(Element elmTable, Table tableObj) {
                NodeList nodes = elmTable.getElementsByTagName("ValueElement");

                for (int i = 0; i < nodes.getLength(); i++) {
                        Element elmCode = (Element) nodes.item(i);

                        Code codeObj = new Code();

                        codeObj.setValue(elmCode.getAttribute("Value"));
                        codeObj.setLabel(elmCode.getAttribute("DisplayName"));

                        if (elmCode.getAttribute("CodeSystem") != null && !elmCode.getAttribute("CodeSystem").equals("")) codeObj.setCodeSystem(elmCode.getAttribute("CodeSystem"));
                        if (elmCode.getAttribute("CodeSystemVersion") != null && !elmCode.getAttribute("CodeSystemVersion").equals("")) codeObj.setCodeSystemVersion(elmCode.getAttribute("CodeSystemVersion"));
                        if (elmCode.getAttribute("Comments") != null && !elmCode.getAttribute("Comments").equals("")) codeObj.setComments(elmCode.getAttribute("Comments"));

                        if (elmCode.getAttribute("Usage") != null && !elmCode.getAttribute("Usage").equals("")) {
                                codeObj.setCodeUsage(elmCode.getAttribute("Usage"));
                        } else {
                                codeObj.setCodeUsage("R");
                        }

                        tableObj.addCode(codeObj);
                }

        }

        private Document stringToDom(String xmlSource) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                factory.setIgnoringComments(false);
                factory.setIgnoringElementContentWhitespace(true);
                DocumentBuilder builder;
                try {
                        builder = factory.newDocumentBuilder();
                        return builder.parse(new InputSource(new StringReader(xmlSource)));
                } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                } catch (SAXException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return null;
        }

}
