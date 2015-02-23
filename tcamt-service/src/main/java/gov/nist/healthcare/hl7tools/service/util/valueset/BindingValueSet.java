package gov.nist.healthcare.hl7tools.service.util.valueset;

import gov.nist.healthcare.hl7tools.domain.Code;
import gov.nist.healthcare.hl7tools.domain.CodeSystem;
import gov.nist.healthcare.hl7tools.domain.Component;
import gov.nist.healthcare.hl7tools.domain.Datatype;
import gov.nist.healthcare.hl7tools.domain.Field;
import gov.nist.healthcare.hl7tools.domain.Segment;
import gov.nist.healthcare.hl7tools.domain.Usage;
import gov.nist.healthcare.hl7tools.domain.ValueSet;
import gov.nist.healthcare.hl7tools.v2.maker.core.ConversionException;
import gov.nist.healthcare.hl7tools.v2.maker.core.domain.profile.MessageProfile;
import gov.nist.healthcare.hl7tools.v2.profilemaker.service.JSONConverterService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import au.com.bytecode.opencsv.CSVReader;

public class BindingValueSet {

	private MessageProfile profile;
	private Document valueSetDom;
	private List<String[]> bindingDTCSV;
	private List<String[]> bindingSegmentCSV;
	
	public void uploadProfile(String profilePath) throws ConversionException, IOException{
		File profileFile = new File(profilePath);
	    InputStream is = new FileInputStream(profileFile);
		JSONConverterService jConverterService = new JSONConverterService();
		this.setProfile(jConverterService.fromStream(is));
	}
	
	public void downloadProfile(String targetProfilePath) throws ConversionException, IOException{
		JSONConverterService jConverterService = new JSONConverterService();
		
		
		InputStream inputStream = jConverterService.toStream(this.profile);
		OutputStream outputStream = new FileOutputStream(new File(targetProfilePath));
 
		int read = 0;
		byte[] bytes = new byte[1024];
 
		while ((read = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}
 
		System.out.println(targetProfilePath + "--------------------Done!");
	    outputStream.close();
	}
	
	public void uploadValueSet(String valueSetPath) throws IOException{
		String valueSetStr = new String(Files.readAllBytes(Paths.get(valueSetPath)));
		this.setValueSetDom(this.stringToDom(valueSetStr));
	}
	
	public void uploadBindingCSVForDT(String bindingCSVPath) throws IOException{
		CSVReader csvReader = new CSVReader(new FileReader(bindingCSVPath));
		List<String[]> paraCSV = new ArrayList<String[]>();
		paraCSV = csvReader.readAll();
		paraCSV.remove(0);
		
		this.setBindingDTCSV(paraCSV);
		
		csvReader.close();
	}
	
	public void uploadBindingCSVForSegment(String bindingCSVPath) throws IOException{
		CSVReader csvReader = new CSVReader(new FileReader(bindingCSVPath));
		List<String[]> paraCSV = new ArrayList<String[]>();
		paraCSV = csvReader.readAll();
		paraCSV.remove(0);
		
		this.setBindingSegmentCSV(paraCSV);
		
		csvReader.close();
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
	private void bindingSEGMENTtoProfile() {
		removeExsitingBindingForSegment();
		
		for(String[] sarray:this.bindingSegmentCSV){
			String key = sarray[0].trim();
			ValueSet vs = this.findTable(key);
			
			for(int i=1; i<sarray.length; i++){				
				if(sarray[i] != null && !sarray[i].equals("")){
					Set<Segment> segments = this.profile.getSegmentSet();
					
					for(Segment segment:segments){
						if(segment.getRoot() != null){
							String path = sarray[i].trim();
							
							if(path.split("\\.").length == 2){
								String segmentName = path.split("\\.")[0];
								int fieldPosition = Integer.parseInt(path.split("\\.")[1]);
								
								
								if(segment.getRoot().equals(segmentName)){
									Field f = segment.getFields().get(fieldPosition-1);
									f.setCodeTable(vs);
								}
							}else if(path.split("\\.").length == 3){
								//TODO
							}
						}
						
					}
					
				}
			}
			
		}
	}
	
	private void bindingDTtoProfile() {
		this.removeExsitingBindingForDT();
		
		for(String[] sarray:bindingDTCSV){
			String key = sarray[0].trim();
			ValueSet vs = this.findTable(key);
			
			for(int i=1; i<sarray.length; i++){				
				if(sarray[i] != null && !sarray[i].equals("")){
					Set<Datatype> dts = this.profile.getDatatypeSet();
					
					for(Datatype dt:dts){			
						if(dt.getRoot() != null){
							String path = sarray[i].trim();
							
							if(path.split("\\.").length == 2){
								String dtName = path.split("\\.")[0];
								int componentPosition = Integer.parseInt(path.split("\\.")[1]);
								if(dt.getRoot().equals(dtName)){
									Component c = dt.getComponents().get(componentPosition-1);
									
									
									c.setCodeTable(vs);
									
								}
							}else if(path.split("\\.").length == 3){
								//TODO
							}
						}
						
					}
					
				}
			}
			
		}
		
	}

	private void removeExsitingBindingForSegment() {
		Set<Segment> segs = this.profile.getSegmentSet();
		
		for(Segment seg:segs){
			if(seg.getFields() != null){
				for(Field f:seg.getFields()){
					if(f.getCodeTable()!=null)
						if(f.getCodeTable().getId() != null){
							f.setCodeTable(null);
						}
				}
			}
		}
	}
	
	private void removeExsitingBindingForDT() {
		Set<Datatype> dts = this.profile.getDatatypeSet();
		
		for(Datatype dt:dts){
			if(dt.getComponents() != null){
				for(Component c:dt.getComponents()){
					if(c.getCodeTable()!=null)
						if(c.getCodeTable().getId() != null){
							c.setCodeTable(null);
						}
				}
			}
		}
	}

	private ValueSet findTable(String key) {
		NodeList nodes = this.valueSetDom.getElementsByTagName("TableDefinition");
		
		for(int i=0; i<nodes.getLength(); i++){
			Element elm = (Element)nodes.item(i);
			
			if(elm.getAttribute("Id").equals(key)){
				ValueSet vs = new ValueSet();
				
				vs.setComment("Automatic Binding Table");
				vs.setDescription(elm.getAttribute("Name"));
				vs.setId(elm.getAttribute("Id"));
				vs.setKey(elm.getAttribute("Id"));
				vs.setName(elm.getAttribute("Id"));
				vs.setOid(elm.getAttribute("Oid"));
				vs.setVersion(elm.getAttribute("Version"));
				
				List<Code> codeList = new ArrayList<Code>();
				
				NodeList codeNodes = elm.getElementsByTagName("TableElement");
				for(int j=0;j<codeNodes.getLength();j++){
					Element codeElm = (Element)codeNodes.item(j);
					Code code = new Code();
					CodeSystem codeSystem = new CodeSystem();
					codeSystem.setId(codeElm.getAttribute("Codesys"));
					codeSystem.setKey(codeElm.getAttribute("Codesys"));
					codeSystem.setName(codeElm.getAttribute("Codesys"));
					code.setCodeSystem(codeSystem);
					code.setComment("");
					code.setDescription(codeElm.getAttribute("DisplayName"));
					code.setUsage(Usage.R);
					code.setValue(codeElm.getAttribute("Code"));
					codeList.add(code);
					
				}
				
				vs.setCodes(codeList);
				
				return vs;
			}
		}
		
		return null;
	}

	public MessageProfile getProfile() {
		return profile;
	}

	public void setProfile(MessageProfile profile) {
		this.profile = profile;
	}

	public Document getValueSetDom() {
		return valueSetDom;
	}
	
	public void setValueSetDom(Document valueSetDom) {
		this.valueSetDom = valueSetDom;
	}

	public List<String[]> getBindingDTCSV() {
		return bindingDTCSV;
	}

	public void setBindingDTCSV(List<String[]> bindingDTCSV) {
		this.bindingDTCSV = bindingDTCSV;
	}

	public List<String[]> getBindingSegmentCSV() {
		return bindingSegmentCSV;
	}

	public void setBindingSegmentCSV(List<String[]> bindingSegmentCSV) {
		this.bindingSegmentCSV = bindingSegmentCSV;
	}
	
	public static void main(String[] args) throws IOException, ConversionException {
		BindingValueSet bindingValueSet = new BindingValueSet();
		bindingValueSet.uploadValueSet("src//main//resources//IZ//IZ ValueSets.xml");
		bindingValueSet.uploadBindingCSVForDT("src//main//resources//IZ//DT.csv");
		
		//ACK_IZ23-PROFILE-NIST.PROFILE <--------- Z23
		bindingValueSet.uploadProfile("src//main//resources//IZ//IZ_V1.5_ACK_Z23-PROFILE-NIST.PROFILE");
		bindingValueSet.uploadBindingCSVForSegment("src//main//resources//IZ//Z23.csv");
		
		bindingValueSet.bindingDTtoProfile();
		bindingValueSet.bindingSEGMENTtoProfile();
		
		bindingValueSet.downloadProfile("src//main//resources//IZ//result//IZ_V1.5_ACK_Z23-PROFILE-NIST.PROFILE");
		

		//IZ_V.5_QBP_Z34-PROFILE-NIST.PROFILE <--------- Z34
		bindingValueSet.uploadProfile("src//main//resources//IZ//IZ_V1.5_QBP_Z34-PROFILE-NIST.PROFILE");
		bindingValueSet.uploadBindingCSVForSegment("src//main//resources//IZ//Z34.csv");
		
		bindingValueSet.bindingDTtoProfile();
		bindingValueSet.bindingSEGMENTtoProfile();
		
		bindingValueSet.downloadProfile("src//main//resources//IZ//result//IZ_V1.5_QBP_Z34-PROFILE-NIST.PROFILE");
		
		//IZ_V1.5_QBP_Z44-PROFILE-NIST.PROFILE <--------- Z44
		bindingValueSet.uploadProfile("src//main//resources//IZ//IZ_V1.5_QBP_Z44-PROFILE-NIST.PROFILE");
		bindingValueSet.uploadBindingCSVForSegment("src//main//resources//IZ//Z44.csv");
		
		bindingValueSet.bindingDTtoProfile();
		bindingValueSet.bindingSEGMENTtoProfile();
		
		bindingValueSet.downloadProfile("src//main//resources//IZ//result//IZ_V1.5_QBP_Z44-PROFILE-NIST.PROFILE");
		
		//IZ_V1.5_RSP_Z31_Nov3-PROFILE-NIST.PROFILE <--------- Z31
		bindingValueSet.uploadProfile("src//main//resources//IZ//IZ_V1.5_RSP_Z31-PROFILE-NIST.PROFILE");
		bindingValueSet.uploadBindingCSVForSegment("src//main//resources//IZ//Z31.csv");
		
		bindingValueSet.bindingDTtoProfile();
		bindingValueSet.bindingSEGMENTtoProfile();
		
		bindingValueSet.downloadProfile("src//main//resources//IZ//result//IZ_V1.5_RSP_Z31-PROFILE-NIST.PROFILE");
		
		//IZ_V1.5_RSP_Z33-PROFILE-NIST.PROFILE <--------- Z33
		bindingValueSet.uploadProfile("src//main//resources//IZ//IZ_V1.5_RSP_Z33-PROFILE-NIST.PROFILE");
		bindingValueSet.uploadBindingCSVForSegment("src//main//resources//IZ//Z33.csv");
		
		bindingValueSet.bindingDTtoProfile();
		bindingValueSet.bindingSEGMENTtoProfile();
		
		bindingValueSet.downloadProfile("src//main//resources//IZ//result//IZ_V1.5_RSP_Z33-PROFILE-NIST.PROFILE");
		
		//IZ_V1.5_RSP_Z42-PROFILE-NIST.PROFILE <--------- Z42
		bindingValueSet.uploadProfile("src//main//resources//IZ//IZ_V1.5_RSP_Z42-PROFILE-NIST.PROFILE");
		bindingValueSet.uploadBindingCSVForSegment("src//main//resources//IZ//Z42.csv");
		
		bindingValueSet.bindingDTtoProfile();
		bindingValueSet.bindingSEGMENTtoProfile();
		
		bindingValueSet.downloadProfile("src//main//resources//IZ//result//IZ_V1.5_RSP_Z42-PROFILE-NIST.PROFILE");
		
		//IZ_VXU_1.5_IZ22-PROFILE-NIST.PROFILE <--------- Z22
		bindingValueSet.uploadProfile("src//main//resources//IZ//IZ_V1.5_VXU_Z22-PROFILE-NIST.PROFILE");
		bindingValueSet.uploadBindingCSVForSegment("src//main//resources//IZ//Z22.csv");
		
		bindingValueSet.bindingDTtoProfile();
		bindingValueSet.bindingSEGMENTtoProfile();
		
		bindingValueSet.downloadProfile("src//main//resources//IZ//result//IZ_V1.5_VXU_Z22-PROFILE-NIST.PROFILE");
		
		//IZ_VXU_1.5_IZ22-PROFILE-NIST.PROFILE <--------- Z32
		bindingValueSet.uploadProfile("src//main//resources//IZ//IZ_V1.5_RSP_Z32-PROFILE-NIST.PROFILE");
		bindingValueSet.uploadBindingCSVForSegment("src//main//resources//IZ//Z32.csv");
		
		bindingValueSet.bindingDTtoProfile();
		bindingValueSet.bindingSEGMENTtoProfile();
		
		bindingValueSet.downloadProfile("src//main//resources//IZ//result//IZ_V1.5_RSP_Z32-PROFILE-NIST.PROFILE");

	}

}
