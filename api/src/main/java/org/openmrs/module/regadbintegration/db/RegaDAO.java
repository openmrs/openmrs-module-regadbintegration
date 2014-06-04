package org.openmrs.module.regadbintegration.db;

import java.util.List;

import org.openmrs.module.regadbintegration.RegaData;

public interface RegaDAO {
	
	public String addConcept(int conceptId);
	
	public List<Integer> getConcepts();
	
	public void createRegaData(RegaData regaData);
	
	public List<RegaData> getRegaData();
	
	public void deleteRegaData(RegaData regaData);
	
}
