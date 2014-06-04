<ul id="menu">
	<li class="first">
		<a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
	</li>

		<li <c:if test='<%= request.getRequestURI().contains("Concepts") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/regadbintegration/addRelatedConcepts.form">
				<spring:message code="regadbintegration.Concept.addRelated"/>
			</a>
		</li>
		
		<li <c:if test='<%= request.getRequestURI().contains("Export") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/regadbintegration/patientBulkExport.form">
				<spring:message code="Export Patients"/>
			</a>
		</li>
	
	
	
</ul>