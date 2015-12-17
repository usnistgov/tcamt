package gov.nist.healthcare.tcamt.service;

//import gov.nist.healthcare.unified.enums.Context;
//import gov.nist.healthcare.unified.model.EnhancedReport;
//import gov.nist.healthcare.unified.proxy.ValidationProxy;
//import hl7.v2.validation.content.ConformanceContext;
//import hl7.v2.validation.content.DefaultConformanceContext;
//import hl7.v2.validation.vs.ValueSetLibrary;
//import hl7.v2.validation.vs.ValueSetLibraryImpl;
//
//import java.io.InputStream;
//import java.io.Serializable;
//import java.util.Arrays;
//import java.util.List;
//
//import org.apache.commons.io.IOUtils;
//
//public class ValidationMessage implements Serializable {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 5216453472380955109L;
//
//	public void er7MessageValidation(String er7Message, String profile, String constraints, String valueSets, String oid) {
//		try{
//			ValidationProxy vp = new ValidationProxy("TCAMT Instant Testing", "NIST");
//			InputStream csis = IOUtils.toInputStream(constraints, "UTF-8");
//			List<InputStream> confContexts = Arrays.asList(csis);
//			ConformanceContext conformanceContext = DefaultConformanceContext.apply(confContexts).get();
//			InputStream vsLibXMLis = IOUtils.toInputStream(valueSets, "UTF-8");
//			ValueSetLibrary valueSetLibrary = ValueSetLibraryImpl.apply(vsLibXMLis).get();
//			
//			EnhancedReport report = vp.validate(er7Message, profile, conformanceContext, valueSetLibrary, oid, Context.Free);
//			String resultHTML = report.to("html").toString();
//			System.out.println(resultHTML);
//		}catch (Exception e){
//			
//		}
//		
//	}
//}
