package org.openmrs.module.regadbintegration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.UserContext;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.openmrs.util.OpenmrsConstants;

public class RegaTimer extends AbstractTask{
	protected final Log log = LogFactory.getLog(getClass());
	RegaService regaService=Context.getService(RegaService.class);
	ObsService obsService=Context.getObsService();
	public static int count = 0;
	public static UserContext usercontext;

	@Override
	public void execute() {
		log.info("Task is running");
		Context.setUserContext(usercontext);
		Context.openSession();
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSONS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_ADD_PERSONS);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_EDIT_PERSONS);
		AdministrationService as=Context.getAdministrationService();
		String ip=as.getGlobalProperty("regadbintegration.remoteinstanceaddress");
		String username=as.getGlobalProperty("regadbintegration.regadbusername");
		String password=as.getGlobalProperty("regadbintegration.regadbpassword");
		if(checkConnection(ip)){
			List<RegaData> stagingRegaData=regaService.getRegaData();
			List<Integer> fullExportPatientList=new ArrayList<Integer>();
			Map<Integer,List<Obs>> obsMap=new HashMap<Integer, List<Obs>>();
			List<RegaData> toBeDeletedFE=new ArrayList<RegaData>();
			Map<Integer,List<RegaData>> toBeDeletednonFE=new HashMap<Integer,List<RegaData>>();
			try{
				for(RegaData rd:stagingRegaData)
				{
					if(rd.getFullExport())
					{
						if(!fullExportPatientList.contains(rd.getPatientId()))
							fullExportPatientList.add(rd.getPatientId());
						toBeDeletedFE.add(rd);
					}
					else
					{
						int key=rd.getPatientId();
						List<Obs> tempObsList=new ArrayList<Obs>();
						if(obsMap.containsKey(key))
						{
							tempObsList=obsMap.get(key);
						}
						Obs obs;
						if(!(obs=obsService.getObs(rd.getObsId())).getVoided())
							tempObsList.add(obs);
						obsMap.put(key, tempObsList);

						List<RegaData> tempRegaList=new ArrayList<RegaData>();
						if(toBeDeletednonFE.containsKey(key))
						{
							tempRegaList=toBeDeletednonFE.get(key);
						}
						tempRegaList.add(rd);
						toBeDeletednonFE.put(key,tempRegaList);
					}
				}
				int[] integerarray=new int[obsMap.keySet().size()];
				Iterator<Integer> obsIterator=obsMap.keySet().iterator();
				int k=0;
				while(obsIterator.hasNext()){
					integerarray[k]=obsIterator.next();
					k++;
				}
				for(int j:integerarray)
					if(fullExportPatientList.contains(j)){
						obsMap.remove(j);
						toBeDeletedFE.addAll(toBeDeletednonFE.get(j));
						toBeDeletednonFE.remove(j);
					}
				if(!fullExportPatientList.isEmpty())
					{
						String[] patientIds=new String[fullExportPatientList.size()];
						for(int i=0;i<fullExportPatientList.size();i++)
							patientIds[i]=fullExportPatientList.get(i)+"";
						try {

							boolean success=regaService.doBulkExport(patientIds, ip,username,password);
							if(success){
								deleteData(toBeDeletedFE);
							}
						} catch (IOException e) {
							log.info("Exception occured while doing bulk export");
						}
					}
					if(!obsMap.isEmpty())
					{

						try {

							boolean success=regaService.exportObsCsv(obsMap, ip, username, password);
							if(success){
								deleteData(toBeDeletednonFE.values());
							}
						}
						catch (IOException e) {
							log.info("Exception occured while doing obs export");
						}
					}
				}
			catch(APIAuthenticationException apiAuthenticationException){
				log.info("error generated",apiAuthenticationException);
			}
			finally{
			removeAndClose();	
			}
		}
		else
			log.info("Unable to establish a connection");
			removeAndClose();

	}
	private void deleteData(Collection<List<RegaData>> values) {
		for(List<RegaData> lrd:values)
		{
			for(RegaData rd:lrd)
			{
				regaService.deleteRegaData(rd);
			}
		}

	}
	public void deleteData(List<RegaData> toBeDeleted)
	{
		for(int i=0;i<toBeDeleted.size();i++){
			regaService.deleteRegaData(toBeDeleted.get(i));
		}
	}
	private void removeAndClose()
	{
		try{
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSONS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PATIENTS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_OBS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_ADD_PERSONS);
			Context.removeProxyPrivilege(OpenmrsConstants.PRIV_EDIT_PERSONS);
			Context.closeSession();
			}
			catch(APIException ae){}
	}
	public boolean checkConnection(String ip) {
		boolean isActive=true;
		Socket theSock = new Socket();
		try {
			InetAddress addr = InetAddress.getByName(ip);
			SocketAddress sockaddr = new InetSocketAddress(addr,8080);
			theSock.connect(sockaddr,5000);
		} catch (UnknownHostException e) {
			isActive=false;
		} catch (SocketTimeoutException e) {
			isActive=false;
		} catch (IOException e) {
			isActive=false;
		} finally {
			if (theSock != null) {
				try {
					theSock.close();
				} catch (IOException e) {
				}
			}
		}
		return isActive;
	}
}
