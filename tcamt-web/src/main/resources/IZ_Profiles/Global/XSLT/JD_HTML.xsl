<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:util="http://hl7.nist.gov/juror-doc/util" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    exclude-result-prefixes="xs util">
    <xsl:output method="xhtml" encoding="UTF-8" indent="yes"/>
    <xsl:template name="commentTemplate">

        <td bgcolor="#F2F2F2">
            <!--    <div contentEditable="true"
                style="width: 100%; height: 100%; border: none; resize: none; max-width: 300px">
                <xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;</div> -->
            <textarea maxLength="100"
                style="width: 100%; height: 100%; border: 1px; background: 1px  #F2F2F2; resize:vertical; overflow-y:hidden "> </textarea>

        </td>

    </xsl:template>

    <xsl:template name="testExistence">
        <xsl:param name="node"/>
        <xsl:choose>
            <xsl:when test="exists($node)">
                <td>
                    <xsl:value-of select="$node"/>
                </td>
            </xsl:when>
            <xsl:otherwise>
                <td bgcolor="#D2D2D2"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="testExistence-with-date">
        <xsl:param name="node"/>
        <xsl:choose>
            <xsl:when test="exists($node)">
                <xsl:choose>
                    <xsl:when test="$node != ''">
                        <td>
                            <xsl:value-of select="util:format-date($node)"/>

                        </td>
                    </xsl:when>
                    <xsl:otherwise>
                        <td bgcolor="#D2D2D2"/>
                    </xsl:otherwise>
                </xsl:choose>

            </xsl:when>
            <xsl:otherwise>
                <td bgcolor="#D2D2D2"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="followSibling-with-date">
        <xsl:param name="node2"/>
        <xsl:choose>
            <xsl:when test="$node2 != ''">
                <td>
                    <xsl:value-of select="util:format-date($node2)"/>

                </td>
            </xsl:when>
            <xsl:otherwise>
                <td bgcolor="#D2D2D2"/>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>
    <!--   <xsl:template name="dateTime">
        <xsl:param name="dateS"/>
        <xsl:variable name="dateformat"
            select="
                xs:date(concat(
                substring($dateS, 1, 4), '-',
                substring($dateS, 5, 2), '-',
                substring($dateS, 7, 2)
                ))"/>
        <xsl:value-of select="format-date($dateformat, '[M01]/[D01]/[Y0001]')"/>
    </xsl:template>-->

    <xsl:function name="util:format-date">
        <xsl:param name="elementDataIn"/>
        <xsl:variable name="elementData" select="concat($elementDataIn, '                ')"/>
        <xsl:if test="string-length(normalize-space($elementData)) > 0">
            <xsl:variable name="year" select="substring($elementData, 1, 4)"/>
            <xsl:variable name="month" select="concat(substring($elementData, 5, 2), '/')"/>
            <xsl:variable name="day" select="concat(substring($elementData, 7, 2), '/')"/>
            <xsl:value-of select="concat($month, $day, $year)"/>

        </xsl:if>
    </xsl:function>

    <xsl:template match="/">

        <html>
            <head>

                <style type="text/css">
                    @media screen{
                        .jurordocument fieldset{
                            font-size:100%;
                        }
                        .jurordocument table tbody tr th{
                            font-size:90%;
                        }
                        .jurordocument table tbody tr td{
                            font-size:90%;
                        }
                    }
                    @media print{
                        .jurordocument fieldset{
                            font-size:x-small;
                            page-break-inside:avoid;
                        }
                        .jurordocument table{
                            float:none !important;
                            page-break-before:avoid;
                            overflow:visible !important;
                            position:relative;
                        }
                        .jurordocument table tr{
                            page-break-inside:avoid;
                        }
                        .jurordocument table th{
                            font-size:x-small;
                        }
                    
                        .jurordocument table td{
                            font-size:xx-small;
                        }
                        * [type = text]{
                            width:98%;
                            height:15px;
                            margin:2px;
                            padding:0px;
                            background:1px #ccc;
                    
                        }
                        .jurordocument h3{
                            font-size:xx-small;
                        }
                        .jurordocument p{
                            font-size:x-small;
                        }
                    
                    
                        .jurordocument * [type = checkbox]{
                            width:10px;
                            height:10px;
                            margin:2px;
                            padding:0px;
                            background:1px #ccc;
                        }
                    }
                    
                    .jurordocument * [type = text]{
                        width:95%;
                    
                    }
                    
                    
                    .jurordocument fieldset{
                        width:95%;
                        border:1px solid #446BEC;
                    }
                    .embSpace{
                        padding-left:25px;
                    }
                    .noData{
                        background:#D2D2D2;
                    }
                    .jurordocument table{
                        width:98%;
                        border:1px groove;
                        margin:0 auto;
                        page-break-inside:avoid;
                    }
                    .jurordocument table tr{
                        border:1px groove;
                    }
                    .jurordocument table th{
                        border:1px groove;
                    }
                    .jurordocument table td{
                        border:1px groove;
                        empty-cells:show;
                    }
                    .jurordocument table thead{
                        border:1px groove;
                        background:#446BEC;
                        text-align:center;
                        color:white;
                    }
                    .jurordocument table[id = inspectionStatus] thead tr th:last-child{
                        width:2%;
                        color:black;
                    }
                    .jurordocument table[id = inspectionStatus] thead tr th:nth-last-child(2){
                        width:2%;
                        color:black;
                    }
                    .jurordocument table[id = inspectionStatus] thead tr th:nth-last-child(3){
                        width:45%;
                    }
                    .jurordocument table tbody tr th{
                        text-align:center;
                        background:#C6DEFF;
                    }
                    .jurordocument table tbody tr td{
                        text-align:left;
                    }
                    .jurordocument table tbody tr td [type = text]{
                        text-align:left;
                        margin-left:1%;
                    }
                    .jurordocument table caption{
                        font-weight:bold;
                        color:#0840F8;
                    }</style>
                <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
                <script type="text/javascript">
                    $(function(){
                    
                    $("textarea").on("keyup",function (){
                    var h=$(this);
                    h.height(30).height(h[0].scrollHeight);
                    });
                                               
                    });
                </script>
            </head>
            <body>

                <div class="jurordocument">

                    <table>
                        <thead>
                            <tr>
                                <th colspan="2">Evaluated Immunization History and Immunization
                                    Forecast</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <th>Test Case ID</th>

                                <td>
                                    <xsl:value-of select="RSP_K11/@testcaseName"/>
                                </td>

                            </tr>
                            <tr>
                                <th>Juror ID</th>
                                <td>
                                    <input style="background: 1px  #E2E2E2;" type="text"
                                        maxlength="50"/>
                                </td>
                            </tr>
                            <tr>
                                <th>Juror Name</th>
                                <td>
                                    <input style="background: 1px  #E2E2E2;" type="text"
                                        maxlength="50"/>
                                </td>
                            </tr>
                            <tr>
                                <th>HIT System Tested</th>
                                <td>
                                    <input style="background: 1px  #E2E2E2;" type="text"
                                        maxlength="50"/>
                                </td>
                            </tr>
                            <tr>
                                <th>Inspection Date/Time</th>
                                <td>
                                    <input style="background: 1px  #E2E2E2;" type="text"
                                        maxlength="50"/>
                                </td>
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
                                <th>Reason Failed</th>
                                <td>
                                    <input style="background: 1px  #E2E2E2;" type="text"
                                        maxlength="50"/>
                                </td>
                            </tr>
                            <tr>
                                <th>Juror Comments</th>
                                <td>
                                    <input style="background: 1px  #E2E2E2;" type="text"
                                        maxlength="50"/>
                                </td>
                            </tr>

                        </tbody>
                    </table>
                    <xsl:choose>
                        <xsl:when test="//QAK/QAK.2[. = 'OK']">
                            <h3>DISPLAY VERIFICATION</h3>


                            <fieldset>
                                <p>This Test Case-specific Juror Document provides a checklist for
                                    the Tester to use during certification testing for assessing the
                                    EHR technology's ability to display required core data elements
                                    from the information received in the Evaluated Immunization
                                    History and Immunization Forecast Z42 message. Additional data
                                    from the message or from the EHR are permitted to be displayed
                                    by the EHR. Grayed-out fields in the Juror Document indicate
                                    where no data for the data element indicated were included in
                                    the Z42 message for the given Test Case.</p>
                                <p>The format of this Juror Document is for ease-of-use by the
                                    Tester and does not indicate how the EHR display must be
                                    designed.</p>
                                <p>The Evaluated Immunization History and Immunization Forecast data
                                    shown in this Juror Document are derived from the Z42 message
                                    provided with the given Test Case; equivalent data are permitted
                                    to be displayed by the EHR. The column headings are meant to
                                    convey the kind of data to be displayed; equivalent
                                    labels/column headings are permitted to be displayed by the
                                    EHR.</p>
                            </fieldset>
                            <br/>
                            <!-- Patient Information -->
                            <table>
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


                                    <xsl:for-each select="//PID">
                                        <tr>


                                            <xsl:call-template name="testExistence">
                                                <xsl:with-param name="node"
                                                  select="PID.3[1]/PID.3.1"/>
                                            </xsl:call-template>



                                            <xsl:choose>
                                                <xsl:when test="exists(PID.5)">
                                                  <td>
                                                  <xsl:value-of
                                                  select="concat(PID.5/PID.5.2, ' ', PID.5/PID.5.3, ' ', PID.5/PID.5.1/PID.5.1.1)"
                                                  />
                                                  </td>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                </xsl:otherwise>
                                            </xsl:choose>


                                            <xsl:call-template name="testExistence-with-date">
                                                <xsl:with-param name="node" select="PID.7/PID.7.1"/>
                                            </xsl:call-template>
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
                                            <xsl:call-template name="commentTemplate"/>
                                        </tr>

                                    </xsl:for-each>

                                </tbody>
                                <tfoot>
                                    <tr>
                                        <td colspan="5">When displayed in the EHR with the Evaluated
                                            Immunization History and Immunization Forecast, these
                                            patient demographics data may be derived from either the
                                            received immunization message or the EHR patient record.
                                            When displaying demographics from the patient record,
                                            the EHR must be able to demonstrate a linkage between
                                            the demographics in the message (primarily the patient
                                            ID in PID-3.1) and the patient record used for display
                                            to ensure that the message was associated with the
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
                            <xsl:if test="//OBX.3.1[. = '59779-9']">
                                <table>
                                    <thead>
                                        <tr>
                                            <th> Immunization Schedule Used </th>
                                            <th>Tester Comment</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <xsl:call-template name="testExistence">
                                                <xsl:with-param name="node"
                                                  select="distinct-values(//OBX.3.1[. = '59779-9']/../../OBX.5/OBX.5.2)"
                                                />
                                            </xsl:call-template>

                                            <xsl:call-template name="commentTemplate"/>

                                        </tr>
                                    </tbody>
                                </table>
                            </xsl:if>
                            <br/>
                            <!-- Evaluation Immunization history Where RXA.5.1 != 998 go into the below table-->
                            <xsl:if
                                test="exists(//RXA.5.1[. != '998']/../../../RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30956-7'])">

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
                                                  <xsl:call-template name="testExistence">
                                                  <xsl:with-param name="node"
                                                  select="OBX/OBX.5/OBX.5.2"/>
                                                  </xsl:call-template>
                                                  <xsl:call-template name="testExistence">
                                                  <xsl:with-param name="node"
                                                  select="../RXA/RXA.5/RXA.5.2"/>
                                                  </xsl:call-template>
                                                  <xsl:call-template name="testExistence-with-date">
                                                  <xsl:with-param name="node"
                                                  select="../RXA/RXA.3/RXA.3.1"/>
                                                  </xsl:call-template>

                                                  <xsl:choose>
                                                  <xsl:when
                                                  test="exists(following-sibling::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '59781-5']/../..)">
                                                  <xsl:for-each
                                                  select="following-sibling::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '59781-5']/../..">
                                                  <xsl:if test="position() = 1">
                                                  <xsl:choose>
                                                  <xsl:when test="OBX.5/OBX.5.1 = 'Y'">
                                                  <td>YES</td>
                                                  </xsl:when>
                                                  <xsl:when test="OBX.5/OBX.5.1 = 'N'">
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
                                                  <xsl:value-of select="OBX.5/OBX.5.2"/>
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

                                                  <xsl:call-template name="commentTemplate"/>

                                                </tr>
                                            </xsl:for-each>
                                        </xsl:for-each>

                                    </tbody>
                                    <tfoot>
                                        <tr>
                                            <td colspan="7">* "Completion Status" refers to the
                                                status of the dose of vaccine administered on the
                                                indicated date and may be interpreted as "Dose
                                                Status". A status of "Complete" means that the
                                                vaccine dose was "completely administered" as
                                                opposed to "partially administered". </td>
                                        </tr>
                                    </tfoot>
                                </table>

                                <br/>
                            </xsl:if>
                            <!-- Immunization Forecast where RXA.5.1=998 go to the below table-->
                            <xsl:if
                                test="//RXA.5.1[. = '998']/../../../RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30956-7']">
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
                                                  <xsl:call-template name="testExistence">
                                                  <xsl:with-param name="node"
                                                  select="OBX/OBX.5/OBX.5.2"/>
                                                  </xsl:call-template>
                                                  <xsl:choose>

                                                  <xsl:when
                                                  test="exists(following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30980-7']/../..)">
                                                  <xsl:for-each
                                                  select="following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30980-7']/../..">
                                                  <xsl:if test="position() = 1">
                                                  <xsl:call-template name="followSibling-with-date">
                                                  <xsl:with-param name="node2"
                                                  select="OBX.5/OBX.5.1"/>
                                                  </xsl:call-template>
                                                  </xsl:if>
                                                  </xsl:for-each>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                  </xsl:otherwise>
                                                  </xsl:choose>


                                                  <xsl:choose>

                                                  <xsl:when
                                                  test="exists(following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30981-5'])">
                                                  <xsl:for-each
                                                  select="following::RSP_K11.OBSERVATION/OBX/OBX.3/OBX.3.1[. = '30981-5']/../..">
                                                  <xsl:if test="position() = 1">
                                                  <xsl:call-template name="followSibling-with-date">

                                                  <xsl:with-param name="node2"
                                                  select="OBX.5/OBX.5.1"/>


                                                  </xsl:call-template>
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
                                                  <xsl:call-template name="followSibling-with-date">

                                                  <xsl:with-param name="node2"
                                                  select="OBX.5/OBX.5.1"/>


                                                  </xsl:call-template>
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
                                                  <xsl:value-of select="OBX.5/OBX.5.2"/>

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
                                                  <xsl:value-of select="OBX.5/OBX.5.2"/>

                                                  </td>
                                                  </xsl:if>
                                                  </xsl:for-each>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                  </xsl:otherwise>
                                                  </xsl:choose>


                                                  <xsl:call-template name="commentTemplate"/>

                                                </tr>
                                            </xsl:for-each>
                                        </xsl:for-each>


                                    </tbody>

                                </table>
                            </xsl:if>
                        </xsl:when>
                        <xsl:when test="//QAK/QAK.2[. = 'NF']">
                            <h3>DISPLAY VERIFICATION</h3>


                            <fieldset>
                                <p>This Test Case-specific Juror Document provides a checklist for
                                    the Tester to use during certification testing for assessing the
                                    EHR technology's ability to display information notifying the
                                    HIT user that a Return Acknowledgement with No Person Records
                                    Z33 message was received (in response to an Evaluated
                                    Immunization History and Immunization Forecast Z44 query
                                    message). Additional data from the message or from the EHR are
                                    permitted to be displayed by the EHR. </p>
                                <p> The format of this Juror Document is for ease-of-use by the
                                    Tester and does not indicate how the EHR display must be
                                    designed.</p>
                                <p> The data shown in this Juror Document are derived from the test
                                    data provided with the given Test Case; equivalent data are
                                    permitted to be displayed by the EHR. The column headings are
                                    meant to convey the kind of data to be displayed; equivalent
                                    labels/column headings are permitted to be displayed by the EHR. </p>

                            </fieldset>
                            <br/>
                            <table>
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


                                    <xsl:for-each select="//QPD">
                                        <tr>


                                            <xsl:call-template name="testExistence">
                                                <xsl:with-param name="node" select="QPD.3/QPD.3.1"/>
                                            </xsl:call-template>



                                            <xsl:choose>
                                                <xsl:when test="exists(QPD.4)">
                                                  <td>
                                                  <xsl:value-of
                                                  select="concat(QPD.4/QPD.4.2, ' ', QPD.4/QPD.4.3, ' ', QPD.4/QPD.4.1/QPD.4.1.1)"
                                                  />
                                                  </td>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                </xsl:otherwise>
                                            </xsl:choose>


                                            <xsl:call-template name="testExistence-with-date">
                                                <xsl:with-param name="node" select="QPD.6/QPD.6.1"/>
                                            </xsl:call-template>
                                            <xsl:choose>
                                                <xsl:when test="exists(QPD.7)">
                                                  <xsl:choose>
                                                  <xsl:when test="QPD.7 = 'F'">
                                                  <td> Female </td>
                                                  </xsl:when>
                                                  <xsl:when test="QPD.7 = 'M'">
                                                  <td> Male </td>
                                                  </xsl:when>
                                                  </xsl:choose>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <xsl:call-template name="commentTemplate"/>
                                        </tr>

                                    </xsl:for-each>

                                </tbody>
                                <tfoot>
                                    <tr>
                                        <td colspan="5">When displayed in the EHR with the
                                            notification indicating that a Return Acknowledgement
                                            with No Person Records Z33 message was received, these
                                            patient demographics data are derived from the EHR
                                            patient record.</td>
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
                                <tbody>
                                    <tr>
                                        <td style="text-align:center"> The EHR shall display a
                                            notification indicating that the query for an Evaluated
                                            Immunization History and Immunization Forecast is
                                            complete but no matching records were found for the
                                            person in the query. </td>
                                    </tr>
                                </tbody>
                            </table>
                        </xsl:when>
                        <xsl:when test="//QAK/QAK.2[. = 'TM']">
                            <h3>DISPLAY VERIFICATION</h3>


                            <fieldset>
                                <p>This Test Case-specific Juror Document provides a checklist for
                                    the Tester to use during certification testing for assessing the
                                    EHR technology's ability to display information notifying the
                                    HIT user that a Return Acknowledgement with No Person Records
                                    Z33 message was received (in response to an Evaluated
                                    Immunization History and Immunization Forecast Z44 query
                                    message). Additional data from the message or from the EHR are
                                    permitted to be displayed by the EHR. </p>
                                <p>The format of this Juror Document is for ease-of-use by the
                                    Tester and does not indicate how the EHR display must be
                                    designed.</p>
                                <p>The data shown in this Juror Document are derived from the test
                                    data provided with the given Test Case; equivalent data are
                                    permitted to be displayed by the EHR. The column headings are
                                    meant to convey the kind of data to be displayed; equivalent
                                    labels/column headings are permitted to be displayed by the
                                    EHR.</p>


                            </fieldset>
                            <br/>
                            <table>
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


                                    <xsl:for-each select="//QPD">
                                        <tr>


                                            <xsl:call-template name="testExistence">
                                                <xsl:with-param name="node" select="QPD.3/QPD.3.1"/>
                                            </xsl:call-template>



                                            <xsl:choose>
                                                <xsl:when test="exists(QPD.4)">
                                                  <td>
                                                  <xsl:value-of
                                                  select="concat(QPD.4/QPD.4.2, ' ', QPD.4/QPD.4.3, ' ', QPD.4/QPD.4.1/QPD.4.1.1)"
                                                  />
                                                  </td>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                </xsl:otherwise>
                                            </xsl:choose>


                                            <xsl:call-template name="testExistence-with-date">
                                                <xsl:with-param name="node" select="QPD.6/QPD.6.1"/>
                                            </xsl:call-template>
                                            <xsl:choose>
                                                <xsl:when test="exists(QPD.7)">
                                                  <xsl:choose>
                                                  <xsl:when test="QPD.7 = 'F'">
                                                  <td> Female </td>
                                                  </xsl:when>
                                                  <xsl:when test="QPD.7 = 'M'">
                                                  <td> Male </td>
                                                  </xsl:when>
                                                  </xsl:choose>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                  <td bgcolor="#D2D2D2"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <xsl:call-template name="commentTemplate"/>
                                        </tr>

                                    </xsl:for-each>

                                </tbody>
                                <tfoot>
                                    <tr>
                                        <td colspan="5">When displayed in the EHR with the
                                            notification indicating that a Return Acknowledgement
                                            with No Person Records Z33 message was received, these
                                            patient demographics data are derived from the EHR
                                            patient record.</td>
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
                                <tbody>
                                    <tr>
                                        <td style="text-align:center"> The EHR shall display a
                                            notification indicating that the query for an Evaluated
                                            Immunization History and Immunization Forecast is
                                            complete but too many matching records were found for
                                            the person in the query. </td>
                                    </tr>
                                </tbody>
                            </table>
                        </xsl:when>
                    </xsl:choose>
                </div>
            </body>
        </html>

    </xsl:template>
</xsl:stylesheet>
