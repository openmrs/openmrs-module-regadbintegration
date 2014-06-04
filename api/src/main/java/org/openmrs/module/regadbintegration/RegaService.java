package org.openmrs.module.regadbintegration;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.openmrs.Obs;


public interface RegaService {
	public boolean exportPatientCsv(int patientId,String ip,String username,String password) throws IOException;
	
	public  boolean exportObsCsv(Map<Integer,List<Obs>> obsMap,String ip,String username,String password) throws IOException;
	
	public void generateReport(String patientId,String[] algorithms,String ip,String username,String password) throws IOException;

	public boolean doBulkExport(String[] patientIds,String ip,String username,String password) throws IOException;
	
	/**
	 * 
	 * @param conceptid to be added to the database
	 * @return error message if any while adding the concept
	 * @should add the concept to the database 
	 * @should show error if the concept already exists
	 */
	public String addConcept(int conceptid);
	
	/**
	 * 
	 * @return list of existing concepts in the database
	 * @should return the list of existing concepts in the database
	 */
	public List<Integer> getConcepts();

	/**
	 * 
	 * Auto generated method comment
	 * 
	 * @param patientId
	 * @param obsId
	 * @should save RegaData Object To the database
	 */
	public void saveRegaData(String patientId,String obsId);
	
	/**
	 * 
	 * Auto generated method comment
	 * 
	 * @param regaData
	 * @should delete the given RegaData Object
	 */
	public void deleteRegaData(RegaData regaData);
	
	/**
	 * 
	 * Auto generated method comment
	 * 
	 * @return returns the list of RegaDatas in the database
	 * @should return the list of RegaDatas in the database
	 */
	public List<RegaData> getRegaData();
	
		
	public boolean checkConnection(String ip);
	

	
	
}
