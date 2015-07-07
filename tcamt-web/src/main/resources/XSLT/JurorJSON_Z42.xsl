<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:output method="xml" indent="yes"/>
    <xsl:template name="dateTime">
        <xsl:param name="dateS"/>
        <xsl:variable name="dateformat"
            select="
            xs:date(concat(
            substring($dateS, 1, 4), '-',
            substring($dateS, 5, 2), '-',
            substring($dateS, 7, 2)
            ))"/>
        <xsl:value-of select="format-date($dateformat, '[M01]/[D01]/[Y0001]')"/>
    </xsl:template>
    
        
    <xsl:template match="PID">
        <xsl:for-each
            select="PID.3/PID.3.4/PID.3.4.1[. = 'MYEHR']/../..">
            <patientIdentifier>
                <xsl:value-of select="PID.3.1"/>
            </patientIdentifier>
        </xsl:for-each>
        <patientName>  <xsl:value-of
            select="concat(PID.5/PID.5.2, ' ', PID.5/PID.5.3, ' ', PID.5/PID.5.1/PID.5.1.1)"
        /></patientName>
       
            <xsl:choose>
                <xsl:when test="PID.7/PID.7.1 != ''">
                    <DOB>
                        <xsl:call-template name="dateTime">
                            <xsl:with-param name="dateS" select="PID.7/PID.7.1"/>
                        </xsl:call-template>
                    </DOB>
                </xsl:when>
                <xsl:otherwise>
                    <DOB/>
                </xsl:otherwise>
            </xsl:choose>
       
        <xsl:choose>
            <xsl:when test="PID.8 !=''">
                <xsl:choose>
                    <xsl:when test="PID.8 = 'F'">
                        <gender> Female </gender>
                    </xsl:when>
                    <xsl:when test="PID.8 = 'M'">
                        <gender> Male </gender>
                    </xsl:when>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
               <gender/>
            </xsl:otherwise>
        </xsl:choose>
       <testerComment><xsl:text></xsl:text></testerComment>
    </xsl:template>
    <xsl:template match="/">
    <jurorDocument>
        <patientInformation>         
              <xsl:apply-templates select="//PID"/>            
        </patientInformation>  
        <immunizationScheduleUsed>
            <xsl:for-each select="//OBX.3.1[. = '59779-9']/../..">
                <xsl:if test="position() = 1">
                    <immunizationSchedule>
                        <xsl:value-of select="OBX.5/OBX.5.2"/>
                    </immunizationSchedule> 
                    <testerComment><xsl:text></xsl:text></testerComment>
                </xsl:if>
              
            </xsl:for-each>
        </immunizationScheduleUsed>
       
            <xsl:for-each select="//RXA.5.1[. != '998']/../../..">
                <xsl:for-each
                    select="RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30956-7']/../../..">
                 <evaluatedImmunizationHistory>
                     <vaccineGroup><xsl:value-of select="OBX/OBX.5/OBX.5.2"/></vaccineGroup>
                    <vaccineAdministered><xsl:value-of select="../RXA/RXA.5/RXA.5.2"/></vaccineAdministered>
                     <xsl:choose>
                         
                         <xsl:when test="../RXA/RXA.3/RXA.3.1 != ''">
                             <dateAdministered> <xsl:call-template name="dateTime">
                                 <xsl:with-param name="dateS" select="../RXA/RXA.3/RXA.3.1"></xsl:with-param>
                             </xsl:call-template></dateAdministered>
                         </xsl:when>
                         <xsl:otherwise>
                             <dateAdministered/>
                         </xsl:otherwise>
                     </xsl:choose>
                   
                    <xsl:choose>
                        <xsl:when
                            test="exists(following-sibling::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '59781-5']/../..)">
                    <xsl:for-each
                        select="following-sibling::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '59781-5']/../..">
                        <xsl:if test="position() = 1">
                            <xsl:choose>
                                <xsl:when test="OBX.5/OBX.5.1 = 'Y'">
                                    <validDose>YES</validDose>
                                </xsl:when>
                                <xsl:when test="OBX.5/OBX.5.1 = 'N'">
                                    <validDose>NO</validDose>
                                </xsl:when>
                                <xsl:otherwise>
                            <validDose> </validDose>
                        </xsl:otherwise>
                            </xsl:choose>
                        </xsl:if>
                    </xsl:for-each>
                        </xsl:when>
                        
                    </xsl:choose>
                    <xsl:choose>
                        <xsl:when
                            test="exists(following-sibling::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30982-3']/../..)">
                            <xsl:for-each
                                select="following-sibling::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30982-3']/../..">
                                <xsl:if test="position() = 1">
                                    
                                   <validityReason>
                                        <xsl:value-of select="OBX.5/OBX.5.2"/>
                                    </validityReason>
                                    
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <validityReason> </validityReason>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:choose>
                        <xsl:when test="exists(../RXA/RXA.20)">
                            <xsl:choose>
                                <xsl:when test="../RXA/RXA.20 = 'CP'">
                                    <completionStatus> Complete </completionStatus>
                                </xsl:when>
                                <xsl:when test="../RXA/RXA.20 = 'NA'">
                                    <completionStatus> Not Administered </completionStatus>
                                </xsl:when>
                                <xsl:when test="../RXA/RXA.20 = 'PA'">
                                    <completionStatus> Partially Administered </completionStatus>
                                </xsl:when>
                                <xsl:when test="../RXA/RXA.20 = 'RE'">
                                    <completionStatus> Refused </completionStatus>
                                </xsl:when>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:otherwise>
                            <completionStatus > </completionStatus>
                        </xsl:otherwise>
                    </xsl:choose>
                    <testerComment><xsl:text></xsl:text></testerComment>
               </evaluatedImmunizationHistory> </xsl:for-each>
                
            </xsl:for-each>
        <xsl:for-each select="//RXA.5.1[. = '998']/../../..">
            
            <xsl:for-each
                select="RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30956-7']/../../..">
                <immunizationForecast>
                    <vaccineGroup>
                        <xsl:value-of select="OBX/OBX.5/OBX.5.2"/>
                    </vaccineGroup>
                    <xsl:choose>
                        
                        <xsl:when
                            test="exists(following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30980-7']/../..)">
                            <xsl:for-each
                                select="following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30980-7']/../..">
                                <xsl:if test="position() = 1">
                                    <xsl:choose>
                                        
                                    <xsl:when test="OBX.5/OBX.5.1 != ''">
                                       <dueDate> <xsl:call-template name="dateTime">
                                            <xsl:with-param name="dateS" select="OBX.5/OBX.5.1"></xsl:with-param>
                                        </xsl:call-template></dueDate>
                                    </xsl:when>
                                        <xsl:otherwise>
                                            <dueDate/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <dueDate/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:choose>
                        
                        <xsl:when
                            test="exists(following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30981-5']/../..)">
                    <xsl:for-each
                        select="following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30981-5']/../..">
                        <xsl:if test="position() = 1">
                            <xsl:choose>
                                
                                <xsl:when test="OBX.5/OBX.5.1 != ''">
                                    <earlyDate> <xsl:call-template name="dateTime">
                                        <xsl:with-param name="dateS" select="OBX.5/OBX.5.1"></xsl:with-param>
                                    </xsl:call-template></earlyDate>
                                </xsl:when>
                                <xsl:otherwise>
                                    <earlyDate/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:if>
                    </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <earlyDate/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:choose>
                        
                        <xsl:when
                            test="exists(following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '59777-3']/../..)">
                    <xsl:for-each
                        select="following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '59777-3']/../..">
                        <xsl:if test="position() = 1">
                            <xsl:choose>
                                
                                            <xsl:when test="OBX.5/OBX.5.1 != ''">
                                                <latestDate>
                                                  <xsl:call-template name="dateTime">
                                                  <xsl:with-param name="dateS"
                                                  select="OBX.5/OBX.5.1"/>
                                                  </xsl:call-template>
                                                </latestDate>
                                            </xsl:when>
                                <xsl:otherwise>
                                    <latestDate/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:if>
                    </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise>
                        <latestDate/>
                    </xsl:otherwise>
                    </xsl:choose>
                    <xsl:choose>
                        
                    <xsl:when
                        test="exists(following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '59783-1']/../..)">
                        <xsl:for-each
                            select="following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '59783-1']/../..">
                            <xsl:if test="position() = 1">
                                <seriesStatus>
                                    <xsl:value-of select="OBX.5/OBX.5.2"/>
                                    
                                </seriesStatus>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise>
                        <seriesStatus/>
                    </xsl:otherwise>
                    </xsl:choose>
                    <xsl:choose>
                        
                        <xsl:when
                            test="exists(following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30982-3']/../..)">
                            <xsl:for-each
                                select="following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30982-3']/../..">
                                <xsl:if test="position() = 1">
                                    <forecastReason>
                                        <xsl:value-of select="OBX.5/OBX.5.2"/>
                                        
                                    </forecastReason>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <forecastReason/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <testerComment><xsl:text></xsl:text></testerComment>
                </immunizationForecast>
            </xsl:for-each>
        </xsl:for-each>
    </jurorDocument>
    </xsl:template>
</xsl:stylesheet>