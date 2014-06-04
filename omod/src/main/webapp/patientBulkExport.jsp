<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>
<openmrs:require privilege="View Administration Functions" otherwise="/login.htm" redirect="/admin/index.htm"/>
<openmrs:htmlInclude file="/scripts/dojo/dojo.js" />
<html>
<body>
<h2><spring:message code="regadbintegration.Patient.bulkExport"/></h2>	
<br/>
<div id="findPatient">
				<b class="boxHeader"><spring:message code="Select Patient(s) for Export"/></b>
				<div class="box">
				<div dojoType="PatientSearch" widgetId="pSearch" inputId="searchNode" searchLabel="<spring:message code="Patient.searchBox" htmlEscape="true"/>" showAddPatientLink='false' tableHeight="10" ></div>
				<input id="selectall" type="button" value="Select All" onclick="selectAll()"/>
				<input id="deselectall" type="button" value="Deselect All" onclick="deselectAll()"/>
				<input id="addbutton" type="button" value="Add Selected Patients" onclick="addSelection()"/>
				</div>
				
</div>
<br/>
<br/>
<form method="POST">
<div id="selectPatient" >
				<b class="boxHeader"><spring:message code="Selected patients"/></b>
				<div class="box" style="max-height:500px;overflow:auto">
				<spring:message code="The patients added will be displayed here:"/><br/><br/>
				<table id="selectedTable" width="100%"><th width="4.5%"></th><th align="left" width="7.5%"><b>Identifier</b></th><th align="left"><b>Given</b></th><th align="left"><b>Middle</b></th><th align="left"><b>Family Name</b></th><th align="left"><b>Age</b></th><th align="left"><b>Gender</b></th><th align="left"><b>Birthdate</b></th><th width="4.5%"></th></table>
				<br/>
				<div id="funcDiv" style="display:none">
				<input id="selectall" type="button" value="Select All" onclick="selectAllOnTable()"/>
				<input id="deselectall" type="button" value="Deselect All" onclick="deselectAllOnTable()"/>
				<input id="deleteselected" type="button" value="Delete Selected" onclick="deleteSelected()"/>
				<input id="exportselected" type="submit" value="Export Selected" onclick="return validateExport()"/>
				</div>
				</div>
</div>
</form>
<br/><br/><br/><br/><br/><br/>
</body>
<script type="text/javascript">
dojo.require("dojo.widget.openmrs.PatientSearch");
var searchWidget;
function selectAll()
{
	var inputs = document.getElementsByName("findpatientId");
	for (var i=0; i<inputs.length; i++) {
		inputs[i].checked = true;
	}
}
function deselectAll()
{
	var inputs = document.getElementsByName("findpatientId");
	for (var i=0; i<inputs.length; i++) {
		inputs[i].checked = false;		
	}
}
function selectAllOnTable()
{
	var inputs = document.getElementsByName("selectPatientId");
	for (var i=0; i<inputs.length; i++) {
		inputs[i].checked = true;		
	}
}
function deselectAllOnTable()
{
	var inputs = document.getElementsByName("selectPatientId");
	for (var i=0; i<inputs.length; i++) {
		inputs[i].checked = false;		
	}
}
function deleteSelected()
{
	var index;
	var arrayofrowids=new Array();
	var inputs = document.getElementsByName("selectPatientId");
	var table = document.getElementById("selectedTable");
	for (var i=0; i<inputs.length; i++) {
		if (inputs[i].checked == true)
		arrayofrowids[i]=inputs[i].value;
	}
	for (var i=0; i< arrayofrowids.length; i++) {
	for (var j=0; j< table.rows.length; j++) {
	if(arrayofrowids[i]==table.rows[j].id){
		index=j;
		table.deleteRow(index);
		break;}
	}
	
	}
	if(table.rows.length<=1)
		document.getElementById("funcDiv").style.display="none";
}
function validateExport()
{
	var valid=false;
	var inputs = document.getElementsByName("selectPatientId");
	for (var i=0; i<inputs.length; i++) {
		if(inputs[i].checked == true)
			valid=true;		
	}
	if(valid==false)
		alert("select atleast one patient to export");
	return valid;
}
function populateSelectedPatientTable(patientlistitem)
{
	addRow(patientlistitem);
}
function addSelection(e){
	var inputs = document.getElementsByName("findpatientId");
	for (var i=0; i<inputs.length; i++) {
		var input = inputs[i];
		if (input.type == "checkbox"&&input.checked == true) {
			DWRPatientService.getPatient(input.value,populateSelectedPatientTable);
		}
	}
}

var getCheckbox = function(patient) {
	if (typeof patient == "string") return "";
	var td = document.createElement("td");
	var input = document.createElement("input");
	input.type = "checkbox";
	input.name = "findpatientId";
	input.value = patient.patientId;
	td.appendChild(input);
	return td;
}
function addRow(patient) {
	var table = document.getElementById("selectedTable");
	var rowCount = table.rows.length;
	var exists=false;
	for (var i=0; i< table.rows.length; i++) {
	if(patient.patientId==table.rows[i].id)
		exists=true;
	}
	if(exists)
	{
		return;
	}
	var row = table.insertRow(rowCount);
	row.id=patient.patientId;
	var id=row.id;
	if(rowCount%2==0)
		row.className="alt";
	var cell0 = row.insertCell(0);
	var cell1 = row.insertCell(1);
	var cell2 = row.insertCell(2);
	var cell3 = row.insertCell(3);
	var cell4 = row.insertCell(4);
	var cell5 = row.insertCell(5);
	var cell6 = row.insertCell(6);
	var cell7 = row.insertCell(7);
	var cell8 = row.insertCell(8);
	checkboxelement=document.createElement("input");
	checkboxelement.type = "checkbox";
	checkboxelement.name = "selectPatientId";
	checkboxelement.value = patient.patientId;
	cell0.appendChild(checkboxelement);
	cell1.innerHTML = patient.identifier;
	cell2.innerHTML = patient.givenName;
	cell3.innerHTML = patient.middleName;
	cell4.innerHTML = patient.familyName;
	cell5.innerHTML =patient.age;
	if(patient.gender=="F")
	cell6.innerHTML = "<img src=\"${pageContext.request.contextPath}/images/female.gif\" />";
	else
	cell6.innerHTML = "<img src=\"${pageContext.request.contextPath}/images/male.gif\" />";
	cell7.innerHTML =patient.birthdate.getDate()+"/"+(patient.birthdate.getMonth()+1)+"/"+patient.birthdate.getFullYear();
	buttonelement=document.createElement("input");
	buttonelement.type = "button";
	buttonelement.value="delete";
	buttonelement.id=patient.patientId;
	buttonelement.onclick=new Function("deleterow(this)");
	cell8.appendChild(buttonelement);
	if(table.rows.length>1)
		document.getElementById("funcDiv").style.display="";
	
}
function deleterow(button)
{
	var index;
	var table = document.getElementById("selectedTable");
	for (var i=0; i< table.rows.length; i++) {
	if(button.id==table.rows[i].id)
		index=i;
	}
	table.deleteRow(index);
	if(table.rows.length<=1)
		document.getElementById("funcDiv").style.display="none";
}	
dojo.addOnLoad( function() {
	
	searchWidget = dojo.widget.manager.getWidgetById("pSearch");
	document.getElementById("addbutton").style.display="none";
	document.getElementById("selectall").style.display="none";
	document.getElementById("deselectall").style.display="none";
		searchWidget.getCellFunctions = function() {
		var arr = dojo.widget.openmrs.PatientSearch.prototype.getCellFunctions();
		arr.splice(1, 0, getCheckbox);
		arr.splice(0,1);
		return arr;
	};
	searchWidget.rowMouseOver = function() {
		
	};
	dojo.event.topic.subscribe("pSearch/objectsFound", 
			function(msg) {	
			document.getElementById("addbutton").style.display="";
			document.getElementById("selectall").style.display="";
			document.getElementById("deselectall").style.display="";
			}
		);
			
});

</script>
<style type="text/css">
#selectedTable
{
width:100%;
border-collapse:collapse;
}
#selectedTable td, #selectedTable th 
{
font-size:1em;
border:0px solid #98bf21;
padding:3px 7px 2px 7px; 
}
#selectedTable th 
{
font-size:1.1em;
text-align:left;
background-color:#F5F5F5;
color:#000000;
}
#selectedTable tr.alt td 
{
color:#000000;
background-color:#F5F5F5;
}
</style>
</html>