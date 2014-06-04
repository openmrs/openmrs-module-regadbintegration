package org.openmrs.module.regadbintegration.extension.html;


import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;


public class AdminList extends AdministrationSectionExt {
	@Override
    public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	@Override
    public String getTitle() {
		return "RegaDBModule";
	}
	@Override
    public Map<String, String> getLinks() {
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("module/regadbintegration/patientBulkExport.form", "Export Patients");
		map.put("module/regadbintegration/addRelatedConcepts.form", "Add Related Concepts");
		
		return map;
	}
	@Override
    public String getRequiredPrivilege(){
		return "";
			
	}
}
