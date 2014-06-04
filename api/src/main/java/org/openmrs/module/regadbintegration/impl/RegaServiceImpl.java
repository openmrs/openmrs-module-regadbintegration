package org.openmrs.module.regadbintegration.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;

import net.sf.wts.client.WtsClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.regadbintegration.RegaData;
import org.openmrs.module.regadbintegration.RegaService;
import org.openmrs.module.regadbintegration.RegaTimer;
import org.openmrs.module.regadbintegration.db.RegaDAO;
import org.openmrs.util.OpenmrsUtil;



import xls.ExcelTable;

public class RegaServiceImpl implements RegaService
{
	private Log log = LogFactory.getLog(getClass());
	private RegaDAO dao;
	public static Timer timer;
	public static RegaTimer regaTimer;
	public void setDao(RegaDAO dao)
	{
		this.dao = dao;
	}
	public boolean exportPatientCsv(int patientId,String ip,String username,String password) throws IOException {
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(Integer.valueOf(patientId));
		PersonService personService = Context.getPersonService();
		String xlsPathName = OpenmrsUtil.getApplicationDataDirectory()+ File.separator+"result" + patientId + ".xls";
		ExcelTable patientExcelTable = new ExcelTable("dd/mm/yyyy");
		patientExcelTable.create();
		setHeaders(patientExcelTable);
		insertPatientToExcel(patient,patientExcelTable,1);
		patientExcelTable.writeAndFlush(new File(xlsPathName));
		boolean success=doRemoteCall("ExportPatient","import-xls",xlsPathName,ip,username,password,patientId+"","",false);
		PersonAttribute personAttribute = new PersonAttribute();
		personAttribute.setAttributeType(personService.getPersonAttributeTypeByName("RegaExportID"));
		personAttribute.setValue("ID=" + patient.getPatientId());
		Person person=personService.getPerson(Integer.valueOf(patientId));
		person.addAttribute(personAttribute);
		personService.savePerson(person);
		new File(xlsPathName).delete();
		return success;
	}
	public boolean exportObsCsv(Map<Integer,List<Obs>> obsMap,String ip,String username,String password)
	throws IOException
	{
		PatientService ps=Context.getPatientService();
		String appDir=OpenmrsUtil.getApplicationDataDirectory();
		String obsPathName = appDir+File.separator+"resultobs.xls";
		ExcelTable obsExcelTable = new ExcelTable("dd/mm/yyyy");
		obsExcelTable.create();
		setHeaders(obsExcelTable);
		Iterator<Integer> keySetIterator=obsMap.keySet().iterator();
		int maxrows=1,startrow=1;
		while(keySetIterator.hasNext()){
			int key=keySetIterator.next();
			if(!obsMap.get(key).isEmpty()){
			maxrows=addGivenObservationsToExcel(obsExcelTable,ps.getPatient(key),obsMap.get(key),startrow);
			for(int row=startrow;row<maxrows;row++)
			{
				obsExcelTable.setCell(row, 0, ps.getPatient(key).getPatientId());
			}
			startrow=maxrows;
			}
		}
		obsExcelTable.writeAndFlush(new File(obsPathName));
		boolean success=doRemoteCall("ExportObs","import-xls",obsPathName, ip, username, password, "","",false);
		new File(obsPathName).delete();
		return success;

	}
	public void generateReport(String patientId,String[] algorithms,String ip,String username,String password) throws IOException {
		if(algorithms!=null)
		{
			String algoCommaSeparated="";
			for(int j=0;j<algorithms.length;j++)
			{
				if(j!=algorithms.length-1)
					algoCommaSeparated+=algorithms[j]+",";
				else {
					algoCommaSeparated+=algorithms[j];
				}
			}
			doRemoteCall("","generate-report",algoCommaSeparated,ip,username,password,patientId,"",false);
		}
	}
	public boolean doBulkExport(String[] patientIds, String ip, String username,String password) throws IOException {
		ExcelTable patientExcelTable = new ExcelTable("dd/mm/yyyy");
		patientExcelTable.create();
		PatientService patientService = Context.getPatientService();
		String xlsPathName = OpenmrsUtil.getApplicationDataDirectory() + File.separator+"result.xls";
		setHeaders(patientExcelTable);
		for(int maxrows=1,index=0;index< patientIds.length;index++)
		{
			Patient patient = patientService.getPatient(Integer.valueOf(patientIds[index]));
			maxrows=insertPatientToExcel(patient,patientExcelTable,maxrows);
		}
		patientExcelTable.writeAndFlush(new File(xlsPathName));
		boolean success=doRemoteCall("ExportPatient","import-xls",xlsPathName,ip,username,password,"","",true);
		PersonService personService=Context.getPersonService();
		if(success){
			for(String patientId:patientIds){
				PersonAttribute personAttribute = new PersonAttribute();
				personAttribute.setAttributeType(personService.getPersonAttributeTypeByName("RegaExportID"));
				personAttribute.setValue("ID=" + patientId);
				Person person=personService.getPerson(Integer.valueOf(patientId));
				person.addAttribute(personAttribute);
				personService.savePerson(person);
			}
		}
		new File(xlsPathName).delete();

		return success;
	}
	public String addConcept(int conceptId)
	{
		log.info("adding the concept");
		String errormsg = dao.addConcept(conceptId);
		return errormsg;
	}
	public List<Integer> getConcepts() {
		log.info("Retrieving the concepts");
		return dao.getConcepts();
	}

	public void saveRegaData(String patientId,String obsId) {
		RegaData regaData=new RegaData();
		Date d=new Date();
		regaData.setPatientId(Integer.parseInt(patientId));
		regaData.setDateCreated(d);
		if(obsId==null||obsId=="")
			regaData.setFullExport(true);
		else
		{
			regaData.setObsId(Integer.parseInt(obsId));
			regaData.setFullExport(false);
		}
		dao.createRegaData(regaData);
	}
	public void deleteRegaData(RegaData regaData)
	{
		dao.deleteRegaData(regaData);
	}
	public List<RegaData> getRegaData()
	{
		return dao.getRegaData();
	}
	private void setHeaders(ExcelTable excelTable) {
		String[] headers = {"id","birth_date","sex","given_name","family_name","death_date","CD4", "CD4_date", "viral_load", "viral_load_date"};
		for (int col = 0; col < headers.length; ++col) {
			excelTable.setCell(0, col, headers[col]);

		}
	}
	private int insertPatientToExcel(Patient patient,ExcelTable excelTable,int startrow) {
		for(int col=0;col<6;col++){
			if (col == 0) {
				excelTable.setCell(startrow, col, patient.getId());
			} else if (col == 1) {
				String birthDateString = getDateString(patient.getBirthdate());
				excelTable.setCell(startrow, col, birthDateString);
			}
			else if (col == 2) {
				excelTable.setCell(startrow, col, patient.getGender());
			} else if (col == 3) {
				excelTable.setCell(startrow, col, patient.getGivenName());
			} else if (col == 4) {
				excelTable.setCell(startrow, col, patient.getFamilyName()); }
			else if(col==5) {
				if(patient.getDead()){
					String deathDateString = getDateString(patient.getDeathDate());
					excelTable.setCell(startrow, col, deathDateString);
				}
			}
		}
		int maxrows=addPatientObservations(patient,excelTable,startrow);
		if(maxrows==0)
			return startrow+1;
		else {
			return maxrows+startrow;
		}

	}
	private int addPatientObservations(Patient patient, ExcelTable excelTable,int startrow) {

		ObsService os = Context.getObsService();
		PersonService personService = Context.getPersonService();
		ConceptService conceptService = Context.getConceptService();
		Person person = personService.getPerson(Integer.valueOf(patient.getPatientId()));
		List<Integer> conceptList = getConcepts();
		ListIterator<Integer> li = conceptList.listIterator();
		int maxrows = 0;
		while (li.hasNext()) {
			List<Obs> obslist = os.getObservationsByPersonAndConcept(person, conceptService.getConcept((Integer)li.next()));
			ListIterator<Obs> obsIterator = obslist.listIterator();
			if (obslist.size() != 0) {
				if (((Obs)obslist.get(0)).getConcept().getName().getName().equalsIgnoreCase("CD4 COUNT"))
				{
					while (obsIterator.hasNext()) {
						int nextindex = 1;
						Obs obs = null;
						excelTable.setCell(nextindex = obsIterator.nextIndex() + startrow, 6, getAnyNotNullObsValue(obs = (Obs)obsIterator.next()));
						String obsDateString = getDateString(obs.getObsDatetime());
						excelTable.setCell(nextindex, 7, obsDateString);
					}
					if(maxrows<obslist.size())
						maxrows = obslist.size();;
				}
				if (((Obs)obslist.get(0)).getConcept().getName().getName().equalsIgnoreCase("HIV VIRAL LOAD"))
				{
					while (obsIterator.hasNext()) {
						int nextindex = 1;
						Obs obs = null;
						excelTable.setCell(nextindex = obsIterator.nextIndex() + startrow, 8, getAnyNotNullObsValue(obs = (Obs)obsIterator.next()));
						String obsDateString = getDateString(obs.getObsDatetime());
						excelTable.setCell(nextindex, 9, obsDateString);
					}
					if(maxrows<obslist.size())
						maxrows = obslist.size();
				}

			}

		}

		for (int row = startrow; row < maxrows + 1; ++row)
		{
			excelTable.setCell(row, 0, person.getId());
		}
		return maxrows;
	}
	private boolean doRemoteCall(String methodName,String service,String input, String ip, String username, String password,String patientId,String obsId,boolean isBulkExport) throws IOException {
		String appDir=OpenmrsUtil.getApplicationDataDirectory();
		if(checkConnection(ip)){
			int totlen=("methodname="+methodName+"\n").getBytes().length+("username="+username+"\n").getBytes().length+("password="+password+"\n").getBytes().length+(("patientid="+patientId+"\n").getBytes().length);
			byte[] config = new byte[totlen];
			int i=0;
			for(byte b:("methodname="+methodName+"\n").getBytes())
			{
				config[i]=b;
				i++;
			}
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
			WtsClient client = new WtsClient("http://"+ip+":8080/wts/services/",0);
			try 
			{
				log.info("Negotiating acces protocol");
				String challenge = client.getChallenge("public");
				String ticket = client.login("public", challenge, "public", service);

				log.info("Uploading input files");
				if(service.equals("import-xls"))
					client.upload(ticket, service, "excelfile",new File(input));
				if(service.equals("generate-report"))
					client.upload(ticket, service,"algorithmfile",input.getBytes());

				client.upload(ticket, service, "configfile",config);


				log.info("Starting process");
				client.start(ticket, service);

				log.info("Monitoring the process");
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
					if(client.monitorStatus(ticket, service).startsWith("ENDED"))
					{
						finished = true;
					}
				}
				log.info("Downloading the result file");
				if(service.equals("generate-report")){
					client.download(ticket, service, "reportfile",  new File(appDir,"report.rtf"));			
					client.download(ticket, service, "logfile",  new File(appDir,"reportLog.log"));
				}
				else
				{
					client.download(ticket, service, "logfile",  new File(appDir,"ExportLog.log"));
				}
				log.info("Closing the session");
				client.closeSession(ticket, service);

			} 
			catch (RemoteException e) 
			{
				System.out.println("Cannot connect");
				if(service.equals("generate-report")){
					File f;
					if((f=new File(appDir+File.separator+"reportLog.log")).exists())
					{
						f.delete();
					}
					FileWriter fstream=new FileWriter(appDir+File.separator+"ExportLog.log",true);
					BufferedWriter logFile=new BufferedWriter(fstream);
					log.info("No response from the server,verify your server settings");
					logFile.write("No response from the server,verify your server settings");
					logFile.close();
				}
				if(methodName.equals("ExportPatient")&&!isBulkExport)
				{
					saveRegaData(patientId,null);
					return false;
				}
				if(methodName.equals("ExportObs"))
				{
					return false;
				}
				if(isBulkExport)
				{
					return false;
				}

			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
		}
		else{
			if(service.equals("generate-report")){
				File f;
				if((f=new File(appDir+File.separator+"reportLog.log")).exists())
				{
					f.delete();
				}
				FileWriter fstream=new FileWriter(appDir+File.separator+"ExportLog.log",true);
				BufferedWriter logFile=new BufferedWriter(fstream);
				log.info("No response from the server,verify your server settings");
				logFile.write("No response from the server,verify your server settings");
				logFile.close();
			}
			if(methodName.equals("ExportPatient")&&!isBulkExport)
			{
				saveRegaData(patientId,null);
				return false;
			}
			if(methodName.equals("ExportObs"))
			{
				return false;
			}
			if(isBulkExport)
			{
				return false;
			}

		}
		return true;
	}
	private int addGivenObservationsToExcel(ExcelTable excelTable,Patient patient, List<Obs> ObsList, int startrow) {
		ListIterator<Obs> obsIterator=ObsList.listIterator();
		int index1=0,index2=0;
		while(obsIterator.hasNext())
		{
			Obs nextObs=obsIterator.next();
			if(nextObs.getConcept().getName().getName().equalsIgnoreCase("CD4 COUNT"))
			{
				excelTable.setCell(index1+startrow, 6, getAnyNotNullObsValue(nextObs));
				String obsDateString = getDateString(nextObs.getObsDatetime());
				excelTable.setCell(index1+startrow, 7, obsDateString);
				index1++;
			}
			if(nextObs.getConcept().getName().getName().equalsIgnoreCase("HIV VIRAL LOAD"))
			{
				excelTable.setCell(index2+startrow,8, getAnyNotNullObsValue(nextObs));
				String obsDateString = getDateString(nextObs.getObsDatetime());
				excelTable.setCell(index2+startrow, 9, obsDateString);
				index2++;
			}
		}
		return (index1>index2?index1:index2)+startrow;

	}
	private String getAnyNotNullObsValue(Obs o) {
		if (o.getValueCoded() != null)
			return o.getValueCoded().getName().getName();
		if (o.getValueDrug() != null)
			return o.getValueDrug().getName();
		if (o.getValueDatetime() != null)
			return o.getValueDatetime().toString();
		if (o.getValueBoolean() != null)
			return o.getValueBoolean().toString();
		if (o.getValueCodedName() != null)
			return o.getValueCodedName().getName();
		if (o.getValueComplex() != null)
			return o.getValueComplex().toString();
		if (o.getValueNumeric() != null)
			return o.getValueNumeric().toString();
		if (o.getValueText() != null)
			return o.getValueText();
		if (o.getValueModifier() != null)
			return o.getValueModifier();
		return null;
	}

	private String getDateString(Date date)
	{
		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		String day = cl.get(Calendar.DAY_OF_MONTH)+"".length()==1?"0"+cl.get(Calendar.DAY_OF_MONTH):cl.get(Calendar.DAY_OF_MONTH)+"";
		String month = (cl.get(Calendar.MONTH)+1+"").length()==1?"0"+(cl.get(Calendar.MONTH)+1):cl.get(Calendar.MONTH)+1+"";
		String year = cl.get(Calendar.YEAR)+"".length()==1?"0"+cl.get(Calendar.YEAR):cl.get(Calendar.YEAR)+"";
		return day + "/" + month + "/" + year;

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