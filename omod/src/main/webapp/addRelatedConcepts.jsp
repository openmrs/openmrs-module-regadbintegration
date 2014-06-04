<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>
<openmrs:require privilege="View Administration Functions" otherwise="/login.htm" redirect="/admin/index.htm"/>
<html>
<body>
<h2><spring:message code="regadbintegration.Concept.addRelated"/></h2>	
<br/>
<spring:hasBindErrors name="concept">
	<spring:message code="fix.error"/>
	<br/>
</spring:hasBindErrors>
<c:set var="erroroccured" value="false"/>
<b class="boxHeader"><spring:message code="regadbintegration.Concept.select"/></b>
<form method="post" class="box">
<div>
	<table>
		<tr>
			<td><span><spring:message code="Concept" /></span>:</td>
			<spring:bind path="concept.conceptId">
			<td><openmrs_tag:conceptField formFieldName="conceptId" initialValue="" /></td>
			<td><c:if test="${status.errorMessage != ''}"><c:set var="erroroccured" value="true" /><span class="error">${status.errorMessage}</span></c:if></td>
			</spring:bind>
		</tr>
		<tr>
			<td colspan="2">
			<p />
			<input type="button" value="<spring:message code="general.cancel" />" onClick="clearConceptField('conceptId',${erroroccured})"/>
			<input type="submit" value="<spring:message code="general.save" />"/>
			</td>
		</tr>
		</table>
		</div>
</form>
</body>
<script type="text/javascript">
function clearConceptField(conceptField,erroroccured) {

	if ( conceptField.length > 0 ) {
		if(!erroroccured){
		dwr.util.setValue(conceptField, "");
		var conPopup = dojo.widget.manager.getWidgetById("conceptId_selection");
		if ( conPopup ) {
			conPopup.displayNode.innerHTML = "";
			conPopup.setChangeButtonValue();
		}
		}
		else
		history.go(-1);
	}
}
</script>
</html>
