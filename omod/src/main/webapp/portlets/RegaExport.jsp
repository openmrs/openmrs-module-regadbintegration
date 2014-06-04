<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<div class="boxHeader">DataExport and Report</div>
<div class="box">
<form method="POST" >
	<input type="text" name="patientId" value="${patient.patientId}" style="display: none;" />
	<input type="submit"  name="submit" value="Export this Patient" />
	<input type="button" value="View Patient Report" onclick="showHideDiv('selectionForm');hidemsg();"/>
	<c:choose>
		<c:when test="${Exported.getReportSuccessful=='true'}">
		<a id="servletLink" style="display:none;" href="moduleServlet/regadbintegration/FileServlet" target="_top">Download the report</a>
		</c:when>
	</c:choose>
	<div id="selectionForm" style="display:none; border: 1px dashed black; padding: 10px;">
	<strong><spring:message code="View Patient Report"/></strong><br/><br/>
	<c:choose>
		<c:when test="${Exported.getAlgorithmsSuccessful=='true'}">
			<c:choose>
				<c:when test="${not empty Exported.algorithms}">
					<table>
					<tr>
						<td style="vertical-align:text-top;text-align:right;">
							<spring:message code="Select the generation algorithms:"/>
						</td>
						<td>
							<c:forEach items="${Exported.algorithms }" var="algorithm"> 
							<input type="checkbox" name="algorithm" value="${algorithm}" onchange="hideAlgorithmError()" />${algorithm}<br />
							</c:forEach>
						</td>
						<td style="vertical-align:text-top;text-align:left;"><span id="noAlgorithm" style="display:none" class="error"><spring:message code="select atleast one algorithm"/></span></td>
					</tr>
					</table>
					<br/>
					<input style="margin-left:185px;" type="submit" name="submit" onclick="return validateForm()" value="Get Report">
				</c:when> 
				<c:otherwise>
					<span class="error"><spring:message code="cannot show this patient's report"/></span>
					<br/><span style="margin-left: 4px;">Reason: Patient or viral isolate does not exist.</span>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<span class="error"><spring:message code="cannot show this patient's report"/></span>
			<br/><span style="margin-left: 4px;">Reason: server didnt respond.contact your administrator</span>
		</c:otherwise>
	</c:choose>
	</div>
	<br/><br/>
	<c:choose>
		<c:when test="${Exported.exportSuccessful=='true'}">
			<span id="success" style="background-color: lightyellow;border: 1px dashed grey;padding: 1px 6px 1px 6px;margin-left: 4px;margin-right: 4px;vertical-align: middle;"><spring:message code="Export Operation Successful"/></span><br/><br/>
		</c:when>
		<c:when test="${Exported.exportSuccessful=='false'}">
			<span id="failure" class="error"><spring:message code="Export Operation Failed"/></span>
			<br/><span id="reason" style="margin-left: 4px;">Reason: ${Exported.failedReason}</span>
		</c:when>
		<c:otherwise>
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${Exported.getReportSuccessful=='false'}">
			<span id="failure" class="error"><spring:message code="Report Generation Failed"/></span>
			<br/><span id="reason" style="margin-left: 4px;">Reason: ${Exported.failedReason}</span>
		</c:when>
		<c:otherwise>
		</c:otherwise>
	</c:choose>
</form>
</div>
<script type="text/javascript">
			function hidemsg()
			{
				document.getElementById("success").style.display="none";
				document.getElementById("failure").style.display="none";
				document.getElementById("reason").style.display="none";
			}
			function hideAlgorithmError()
			{
				document.getElementById("noAlgorithm").style.display="none";
			}
			window.onload=function(){
				if(document.getElementById("servletLink")!=null)
				openLink();
				}
			function openLink() {
			   document.location = document.getElementById('servletLink').href;
			     
			}
			function validateForm()
			{
				var result=true;
				var checkboxes=document.getElementsByName("algorithm");
				var selectedAtleastOne=false;
				for (var i = 0; i < checkboxes.length; i++) {
					if(checkboxes[i].checked)
			        {
			        	selectedAtleastOne=true; 
			        	break;   
				    } 
				}
				if(!selectedAtleastOne)
				{
					document.getElementById("noAlgorithm").style.display="";
					result=false;
				}
				return result;
			}
</script>



			