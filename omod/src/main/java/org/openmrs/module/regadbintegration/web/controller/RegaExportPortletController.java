package org.openmrs.module.regadbintegration.web.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.wts.client.WtsClient;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.regadbintegration.RegaService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class RegaExportPortletController extends SimpleFormController implements Serializable{

	private static final long serialVersionUID = 1L;
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,HttpServletResponse response, Object command, BindException errors)
	throws Exception {
		AdministrationService as=Context.getAdministrationService();
		String ip=as.getGlobalProperty("regadbintegration.remoteinstanceaddress");
		String username=as.getGlobalProperty("regadbintegration.regadbusername");
		String password=as.getGlobalProperty("regadbintegration.regadbpassword");
		String appDir=OpenmrsUtil.getApplicationDataDirectory();
		String patientId=request.getParameter("patientId");
		String submit=request.getParameter("submit");
		Map<String,Object> exportedMap = new HashMap<String, Object>();
		RegaService regaService= Context.getService(RegaService.class);
		if(submit.equals("Export this Patient")){
			boolean success=regaService.exportPatientCsv(Integer.parseInt(patientId),ip,username,password);
			String error="";
			if(success){
				File f;
				if((f=new File(appDir,"ExportLog.log")).exists())
				{
					exportedMap.put("exportSuccessful",f.length()==0?"true":"false");
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
			else{
				exportedMap.put("exportSuccessful",false);
				error="Server did not respond,patient will be exported later";
			}
			exportedMap.put("failedReason",error);
		}

		if(submit.equals("Get Report"))
		{
			String algorithms[]=request.getParameterValues("algorithm");
			regaService.generateReport(patientId,algorithms,ip,username,password);
			File f;
			if((f=new File(appDir+File.separator+"reportLog.log")).exists())
			{
				exportedMap.put("getReportSuccessful",f.length()==0?"true":"false");
				if(f.length()!=0)
				{
					String error="";
					FileReader fr=new FileReader(f.getAbsoluteFile());
					BufferedReader br=new BufferedReader(fr);
					String line;
					while((line=br.readLine())!=null)
					{
						error+=line;	
					}
					exportedMap.put("failedReason",error);
					br.close();
				}
				f.delete();
			}
		}
		String[] algorithmArray=new String[]{};
		if(regaService.checkConnection(ip)){
		algorithmArray=getAlgorithms(patientId,ip,username,password);
		exportedMap.put("getAlgorithmsSuccessful",true);
		}
		else
		exportedMap.put("getAlgorithmsSuccessful",false);	
		exportedMap.put("algorithms",algorithmArray);
		return new ModelAndView("/module/regadbintegration/portlets/RegaExport","Exported",exportedMap);
	}
	protected Map<String, String> referenceData(HttpServletRequest request, Object obj, Errors err) throws Exception {

		return new HashMap<String, String>();

	}
	@Override
	protected Object formBackingObject(HttpServletRequest request)
	throws Exception {
		String patientId = (String) request.getParameter("patientId");
		String submit=request.getParameter("submit");
		Map<String,Object> exportedMap = new HashMap<String, Object>();
		String [] algorithmArray=new String[]{};
		RegaService regaService=Context.getService(RegaService.class);
		if(submit==null){
			AdministrationService as=Context.getAdministrationService();
			String ip=as.getGlobalProperty("regadbintegration.remoteinstanceaddress");
			String username=as.getGlobalProperty("regadbintegration.regadbusername");
			String password=as.getGlobalProperty("regadbintegration.regadbpassword");
			if(regaService.checkConnection(ip)){
			algorithmArray=getAlgorithms(patientId,ip,username,password);
			exportedMap.put("getAlgorithmsSuccessful",true);
			}
			else
			exportedMap.put("getAlgorithmsSuccessful",false);
			exportedMap.put("algorithms",algorithmArray);
		}
		exportedMap.put("getReportSuccessful","notdefined");
		exportedMap.put("exportSuccessful","notdefined");
		return exportedMap;
	}
	public String[] getAlgorithms(String patientId,String ip,String username,String password) throws IOException
	{	String appDir=OpenmrsUtil.getApplicationDataDirectory();
		int totlen=("username="+username+"\n").getBytes().length+("password="+password+"\n").getBytes().length+(("patientid="+patientId+"\n").getBytes().length);
		byte[] config = new byte[totlen];
		int i=0;
		for(byte b:("username="+username+"\n").getBytes())
		{
			config[i]=b;
			i++;
		}
		for(byte b:("password="+password+"\n").getBytes())
		{
			config[i]=b;
			i++;
		}
		for(byte b:("patientid="+patientId+"\n").getBytes())
		{
			config[i]=b;
			i++;
		}
		WtsClient client = new WtsClient("http://"+ip+":8080/wts/services/");
		final String serviceName="get-algorithms";
		try 
		{
			System.out.println("Negotiating acces protocol");
			String challenge = client.getChallenge("public");
			String ticket = client.login("public", challenge, "public", serviceName);

			System.out.println("Uploading input files");
			client.upload(ticket, serviceName, "configfile",config);

			System.out.println("Starting retrieval of algorithms");
			client.start(ticket, serviceName);

			System.out.println("Monitoring the process");
			boolean finished = false;
			while(!finished)
			{
				try 
				{
					Thread.sleep(200);
				} 
				catch (InterruptedException ie) 
				{
					ie.printStackTrace();
				}
				if(client.monitorStatus(ticket, serviceName).startsWith("ENDED"))
				{
					finished = true;
				}
			}
			System.out.println("Downloading the result file");
			client.download(ticket,serviceName, "listfile",new File(appDir+File.separator+"listfile"));

			System.out.println("Closing the session");
			client.closeSession(ticket, serviceName);
		} 
		catch (RemoteException e) 
		{
			e.printStackTrace();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		String[] algorithmArray=new String[]{};
		if(new File(appDir+File.separator+"listfile").exists()){
			BufferedReader br=new BufferedReader(new FileReader(appDir+File.separator+"listfile"));
			String line;
			if((line=br.readLine())!=null)
				algorithmArray=line.split(",");
			br.close();
			new File(appDir+File.separator+"listfile").delete();
		}
		return algorithmArray;
	}
	
}
