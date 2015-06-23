<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xhtml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:template name="commentTemplate">

        <input style="background: 1px  #E2E2E2;" type="text"/>

    </xsl:template>
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

    <xsl:template match="/">

        <html>
            <head>

                <style type="text/css">
                    @media screen {
                    fieldset {font-size:100%;}
                    fieldset table tbody tr th {font-size:90%}
                    fieldset table tbody tr td {font-size:90%;}
                    }
                    @media print{fieldset {font-size:x-small;} fieldset table  th { font-size:x-small; }
                    fieldset table  td { font-size:xx-small }* [type=text]{
                    width: 98%;
                    height: 15px;
                    margin: 2px;
                    padding: 0px;
                    background: 1px  #ccc;
                    }
                    h2 {font-size:xx-small;}
                    p {font-size:x-small;}
                    
                    * [type=checkbox]{
                    width: 10px;
                    height: 10px;
                    margin: 2px;
                    padding: 0px;
                    background: 1px  #ccc;
                    }}
                    @page {
                    counter-increment: page;
                    
                    @bottom-center {
                    content: "Page " counter(page);
                    }
                    }
                    @page :left {
                    margin-left: 5%;
                    margin-right: 5%;
                    }
                    
                    @page :right {
                    margin-left: 5%;
                    margin-right: 5%;
                    }
                    
                    
                    * [type=text]{
                    width: 95%;
                    }
                    
                    fieldset {page-break-inside: avoid;}
                    fieldset table { width:98%;border: 1px groove; margin:0 auto;page-break-inside: avoid; table-layout: fixed;}
                    fieldset table  tr { border: 1px groove; }
                    fieldset table  th { border: 1px groove; }
                    fieldset table  td { border: 1px groove;empty-cells:show; }
                    fieldset table thead {border: 1px groove;background:#446BEC;text-align:left;}
                    fieldset table thead tr th:last-child {width:2%;}
                    fieldset table thead tr th:nth-last-child(2) {width:2%;}
                    fieldset table thead tr th:nth-last-child(3) {width:45%;}
                    fieldset table tbody tr th {text-align:left;background:#C6DEFF;width:15%}
                    fieldset table tbody tr td {text-align:left}
                    fieldset table tbody tr td [type=text]{text-align:left;margin-left:1%}
                    fieldset table caption {font-weight:bold;color:#0840F8;}
                    
                    fieldset {width:98%;font-weight:bold;border:1px solid #446BEC;}
                    .embSpace {padding-left:25px;}
                    .noData {background:#D2D2D2;}
                </style>
            </head>
            <body>
                <form>
                    <fieldset id="juror">

                        <table id="headerTable">
                            <thead>
                                <tr>
                                    <th colspan="2">Evaluated Immunization History and Immunization
                                        Forecast (RSP Z42) </th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <th>Test Case ID</th>

                                    <td> </td>

                                </tr>
                                <tr>
                                    <th>Inspection Date/Time</th>
                                    <td/>
                                </tr>
                                <tr>
                                    <th>Inspection Settlement</th>
                                    <td>
                                        <table id="inspectionStatus">
                                            <thead>
                                                <tr>
                                                  <th>Pass</th>
                                                  <th>Fail</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <tr>
                                                  <td>
                                                  <input type="checkbox"/>
                                                  </td>
                                                  <td>
                                                  <input type="checkbox"/>
                                                  </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </td>
                                </tr>
                                <tr>
                                    <th>Juror ID</th>
                                    <td><input style="background: 1px  #E2E2E2;" type="text"/></td>
                                </tr>
                                <tr>
                                    <th>Juror Name</th>
                                    <td><input style="background: 1px  #E2E2E2;" type="text"/></td>
                                </tr>
                            </tbody>
                        </table>

                        <h3>DISPLAY VERIFICATION</h3>


                        <fieldset>
                            <p>This Test Case-specific Juror Document provides a checklist for the
                                Tester to use during certification testing for assessing the EHR
                                technology's ability to display required core data elements from the
                                information received in the Evaluated Immunization History and
                                Immunization Forecast Z42 message. Additional data from the message
                                or from the EHR are permitted to be displayed by the EHR. Grayed-out
                                fields in the Juror Document indicate where no data for the data
                                element indicated were included in the Z42 message for the given
                                Test Case.</p>
                            <p>The format of this Juror Document is for ease-of-use by the Tester
                                and does not indicate how the EHR display must be designed.</p>
                            <p>The Evaluated Immunization History and Immunization Forecast data
                                shown in this Juror Document are derived from the Z42 message
                                provided with the given Test Case; equivalent data are permitted to
                                be displayed by the EHR. The column headings are meant to convey the
                                kind of data to be displayed; equivalent labels/column headings are
                                permitted to be displayed by the EHR.</p>
                        </fieldset>
                        <br/>
                        <!-- Patient Information -->
                        <table id="headerTable">
                            <thead>
                                <tr>
                                    <th colspan="5">Patient Information</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <th>Patient Identifier</th>
                                    <th>Patient Name</th>
                                    <th>DOB</th>
                                    <th>Gender</th>
                                    <th>Tester Comment</th>
                                </tr>

                                <xsl:for-each select="//PID.3.1/../..">
                                    <tr>
                                        <xsl:for-each select="PID.3/PID.3.4[. = 'MYEHR']/..">
                                            <xsl:choose>
                                                <xsl:when test="exists(PID.3.1)">

                                                  <td>
                                                  <xsl:value-of select="PID.3.1"/>
                                                  </td>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:for-each>

                                        <td>
                                            <xsl:value-of
                                                select="concat(PID.5/PID.5.2, ' ', PID.5/PID.5.3, ' ', PID.5/PID.5.1)"
                                            />
                                        </td>
                                        <xsl:choose>
                                            <xsl:when test="exists(PID.7)">
                                                <td>
                                                  <xsl:call-template name="dateTime">
                                                  <xsl:with-param name="dateS" select="PID.7"/>
                                                  </xsl:call-template>
                                                </td>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <td bgcolor="#D2D2D2"/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                        <xsl:choose>
                                            <xsl:when test="exists(PID.8)">
                                                <xsl:choose>
                                                  <xsl:when test="PID.8 = 'F'">
                                                  <td> Female </td>
                                                  </xsl:when>
                                                  <xsl:when test="PID.8 = 'M'">
                                                  <td> Male </td>
                                                  </xsl:when>
                                                </xsl:choose>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <td bgcolor="#D2D2D2"/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                        <td>
                                            <xsl:call-template name="commentTemplate"/>
                                        </td>
                                    </tr>


                                </xsl:for-each>
                            </tbody>


                            <tfoot>



                                <tr>
                                    <td style="font-weight:bold;" colspan="5">When displayed in the
                                        EHR with the Evaluated Immunization History and Immunization
                                        Forecast, these patient demographics data may be derived
                                        from either the received immunization message or the EHR
                                        patient record. When displaying demographics from the
                                        patient record, the EHR must be able to demonstrate a
                                        linkage between the demographics in the message (primarily
                                        the patient ID in PID-3.1) and the patient record used for
                                        display to ensure that the message was associated with the
                                        appropriate patient. </td>
                                </tr>
                            </tfoot>
                        </table>
                        <br/>
                        <table>
                            <thead>


                                <tr>
                                    <th>Evaluated Immunization History and Immunization
                                        Forecast</th>
                                </tr>
                            </thead>
                        </table>
                        <!-- Immunization schedule used -->
                        <table>

                            <xsl:for-each select="//OBX.3.1[. = '59779-9']/../..">
                                <xsl:if test="position() = 1">
                                    <xsl:if test="//OBX.3.1[. = '59779-9']">
                                        <thead>
                                            <tr>
                                                <th> Immunization Schedule Used </th>
                                                <th>Tester Comment</th>
                                            </tr>
                                        </thead>

                                        <tr>
                                            <xsl:choose>
                                                <xsl:when test="exists(OBX.5/OBX.5.2)">
                                                  <td>
                                                  <xsl:value-of select="OBX.5/OBX.5.2"/>
                                                  </td>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                </xsl:otherwise>
                                            </xsl:choose>

                                            <td>
                                                <xsl:call-template name="commentTemplate"/>
                                            </td>
                                        </tr>
                                    </xsl:if>
                                </xsl:if>
                            </xsl:for-each>
                        </table>
                        <br/>
                        <!-- Evaluation Immunization history Where RXA.5.1 != 998 go into the below table-->
                        <xsl:if test="//RXA.5.1[. != '998']/../../..">

                            <table>
                                <thead>
                                    <tr>
                                        <th colspan="7">Evaluated Immunization History</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    <tr>
                                        <th>Vaccine Group</th>
                                        <th>Vaccine Administered</th>
                                        <th>Date Administered</th>
                                        <th>Valid Dose</th>
                                        <th>Validity Reason</th>
                                        <th>Completion Status*</th>
                                        <th>Tester Comment</th>
                                    </tr>


                                    <xsl:for-each select="//RXA.5.1[. != '998']/../../..">
                                        <xsl:for-each
                                            select="RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30956-7']/../../..">

                                            <tr>
                                                <xsl:choose>
                                                  <xsl:when test="exists(OBX/OBX.5/OBX.5.2)">
                                                  <td>
                                                  <xsl:value-of select="OBX/OBX.5/OBX.5.2"/>
                                                  </td>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                                <xsl:choose>
                                                  <xsl:when test="exists(../RXA/RXA.5/RXA.5.2)">
                                                  <td>
                                                  <xsl:value-of select="../RXA/RXA.5/RXA.5.2"/>
                                                  </td>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                                <xsl:choose>
                                                  <xsl:when test="exists(../RXA/RXA.3)">
                                                  <td>
                                                  <xsl:call-template name="dateTime">
                                                  <xsl:with-param name="dateS" select="../RXA/RXA.3"
                                                  />
                                                  </xsl:call-template>
                                                  </td>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="exists(following-sibling::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '59781-5']/../..)">
                                                  <xsl:for-each
                                                  select="following-sibling::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '59781-5']/../..">
                                                  <xsl:if test="position() = 1">
                                                  <xsl:choose>
                                                  <xsl:when test="OBX.5 = 'Y'">
                                                  <td>YES</td>
                                                  </xsl:when>
                                                  <xsl:when test="OBX.5 = 'N'">
                                                  <td>NO</td>
                                                  </xsl:when>
                                                  </xsl:choose>
                                                  </xsl:if>
                                                  </xsl:for-each>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                                <xsl:choose>
                                                  <xsl:when
                                                  test="exists(following-sibling::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30982-3']/../..)">
                                                  <xsl:for-each
                                                  select="following-sibling::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30982-3']/../..">
                                                  <xsl:if test="position() = 1">

                                                  <td>
                                                  <xsl:value-of select="OBX.5"/>
                                                  </td>

                                                  </xsl:if>
                                                  </xsl:for-each>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                                <xsl:choose>
                                                  <xsl:when test="exists(../RXA/RXA.20)">
                                                  <xsl:choose>
                                                  <xsl:when test="../RXA/RXA.20 = 'CP'">
                                                  <td> Complete </td>
                                                  </xsl:when>
                                                  <xsl:when test="../RXA/RXA.20 = 'NA'">
                                                  <td> Not Administered </td>
                                                  </xsl:when>
                                                  <xsl:when test="../RXA/RXA.20 = 'PA'">
                                                  <td> Partially Administered </td>
                                                  </xsl:when>
                                                  <xsl:when test="../RXA/RXA.20 = 'RE'">
                                                  <td> Refused </td>
                                                  </xsl:when>
                                                  </xsl:choose>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                                <td>
                                                  <xsl:call-template name="commentTemplate"/>
                                                </td>
                                            </tr>
                                        </xsl:for-each>
                                    </xsl:for-each>

                                </tbody>
                                <tfoot>
                                    <tr>
                                        <td style="font-weight:bold;" colspan="7">* "Completion
                                            Status" refers to the status of the dose of vaccine
                                            administered on the indicated date and may be
                                            interpreted as "Dose Status". A status of "Complete"
                                            means that the vaccine dose was "completely
                                            administered" as opposed to "partially administered".
                                        </td>
                                    </tr>
                                </tfoot>
                            </table>

                            <br/>
                        </xsl:if>
                        <!-- Immunization Forecast where RXA.5.1=998 go to the below table-->
                        <xsl:if test="//RXA.5.1[. = '998']/../../..">
                            <table>
                                <thead>
                                    <tr>
                                        <th colspan="7">Immunization Forecast</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    <tr>
                                        <th>Vaccine Group</th>
                                        <th>Due Date</th>
                                        <th>Earliest Date To Give</th>
                                        <th>Latest Date to Give</th>
                                        <th>Series Status</th>
                                        <th>Forecast Reason</th>
                                        <th>Tester Comment</th>
                                    </tr>
                                    <xsl:for-each select="//RXA.5.1[. = '998']/../../..">

                                        <xsl:for-each
                                            select="RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30956-7']/../../..">

                                            <tr>
                                                <xsl:choose>
                                                  <xsl:when test="exists(OBX/OBX.5/OBX.5.2)">
                                                  <td>
                                                  <xsl:value-of select="OBX/OBX.5/OBX.5.2"/>
                                                  </td>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                                <xsl:choose>

                                                  <xsl:when
                                                  test="exists(following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30980-7']/../..)">

                                                  <xsl:for-each
                                                  select="following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30980-7']/../..">
                                                  <xsl:if test="position() = 1">
                                                  <td>
                                                  <xsl:call-template name="dateTime">
                                                  <xsl:with-param name="dateS" select="OBX.5"/>
                                                  </xsl:call-template>
                                                  </td>
                                                  </xsl:if>
                                                  </xsl:for-each>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                                <xsl:choose>

                                                  <xsl:when
                                                  test="exists(following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30981-5']/../..)">
                                                  <xsl:for-each
                                                  select="following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30981-5']/../..">
                                                  <xsl:if test="position() = 1">
                                                  <td>
                                                  <xsl:call-template name="dateTime">
                                                  <xsl:with-param name="dateS"
                                                  select="OBX.5/OBX.5.2"/>
                                                  </xsl:call-template>
                                                  </td>
                                                  </xsl:if>
                                                  </xsl:for-each>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                                <xsl:choose>

                                                  <xsl:when
                                                  test="exists(following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '59777-3']/../..)">
                                                  <xsl:for-each
                                                  select="following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '59777-3']/../..">
                                                  <xsl:if test="position() = 1">
                                                  <td>
                                                  <xsl:call-template name="dateTime">
                                                  <xsl:with-param name="dateS" select="OBX.5"/>
                                                  </xsl:call-template>
                                                  </td>
                                                  </xsl:if>
                                                  </xsl:for-each>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                                <xsl:choose>

                                                  <xsl:when
                                                  test="exists(following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '59783-1']/../..)">
                                                  <xsl:for-each
                                                  select="following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '59783-1']/../..">
                                                  <xsl:if test="position() = 1">
                                                  <td>
                                                  <xsl:value-of select="OBX.5"/>

                                                  </td>
                                                  </xsl:if>
                                                  </xsl:for-each>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                                <xsl:choose>

                                                  <xsl:when
                                                  test="exists(following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30982-3']/../..)">
                                                  <xsl:for-each
                                                  select="following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30982-3']/../..">
                                                  <xsl:if test="position() = 1">
                                                  <td>
                                                  <xsl:value-of select="OBX.5"/>

                                                  </td>
                                                  </xsl:if>
                                                  </xsl:for-each>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                  </xsl:otherwise>
                                                </xsl:choose>

                                                <td>
                                                  <xsl:call-template name="commentTemplate"/>
                                                </td>
                                            </tr>
                                        </xsl:for-each>
                                    </xsl:for-each>


                                </tbody>

                            </table>
                        </xsl:if>

                    </fieldset>
                </form>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
