package org.openmrs.module.regadbintegration.web.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.regadbintegration.RegaService;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class BulkExportFormController extends SimpleFormController{

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		String appDir=OpenmrsUtil.getApplicationDataDirectory();
		HttpSession httpSession=request.getSession();
		AdministrationService as=Context.getAdministrationService();
		String ip=as.getGlobalProperty("regadbintegration.remoteinstanceaddress");
		String username=as.getGlobalProperty("regadbintegration.regadbusername");
		String password=as.getGlobalProperty("regadbintegration.regadbpassword");
		String patientIds[]=request.getParameterValues("selectPatientId");
		RegaService rs= Context.getService(RegaService.class);
		boolean success=rs.doBulkExport(patientIds, ip, username, password);
		String error="";
		File f;
		if(success){
			if((f=new File(appDir,"ExportLog.log")).exists())
			{
				if(f.length()!=0)
				{
					FileReader fr=new FileReader(f.getAbsoluteFile());
					BufferedReader br=new BufferedReader(fr);
					String line;
					while((line=br.readLine())!=null)
					{
						error+=line;	
					}
					br.close();
				}
				f.delete();
			}
		}
		if(!success)
			error="Server did not respond,try later";
		if (!error.equals(""))
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,"Export Unsuccessfull, Reason:"+error);
		else
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "All Patients Exported Successfully");
		return new ModelAndView(new RedirectView(getSuccessView()));
	}

	@Override
	protected Map<String,Object> referenceData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		
		return new HashMap<String,Object>();
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		
		return "";
	}


}
