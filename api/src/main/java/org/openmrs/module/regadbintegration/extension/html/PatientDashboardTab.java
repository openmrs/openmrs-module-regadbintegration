package org.openmrs.module.regadbintegration.extension.html;

import org.openmrs.module.web.extension.PatientDashboardTabExt;

public class PatientDashboardTab extends PatientDashboardTabExt{

	@Override
	public String getPortletUrl() {
		// TODO Auto-generated method stub
		return "RegaExport";
	}

	@Override
	public String getRequiredPrivilege() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTabId() {
		// TODO Auto-generated method stub
		return "RegaDB";
	}

	@Override
	public String getTabName() {
		// TODO Auto-generated method stub
		return "RegaDB";
	}

}
