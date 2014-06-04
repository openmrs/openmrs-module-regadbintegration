package org.openmrs.module.regadbintegration;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class RegaServiceTest extends BaseModuleContextSensitiveTest{
	
	@Before
	public void runBeforeAllTests() throws Exception {
		executeDataSet("test/org/openmrs/module/regadbintegration/include/rega_temp_staging_dataInitialData.xml");
		executeDataSet("test/org/openmrs/module/regadbintegration/include/rega_related_conceptsInitialData.xml");
	}
	
	/**
	 * @verifies {@link RegaService#addConcept(int)}
	 * test = should add the concept to the database
	 */
	@Test
	public void addConcept_shouldAddTheConceptToTheDatabase() throws Exception {
		
		RegaService rs=Context.getService(RegaService.class);
		rs.addConcept(18019);
		List<Integer> conceptList=rs.getConcepts();
		boolean exists=false;
		for(int i:conceptList){
			if(i==18019)
				exists=true;
		}
		Assert.assertTrue(exists);
	}

	/**
	 * @verifies {@link RegaService#addConcept(int)}
	 * test = should show error if the concept already exists
	 */
	@Test
	public void addConcept_shouldShowErrorIfTheConceptAlreadyExists()
			throws Exception {
		RegaService rs=Context.getService(RegaService.class);
		List<Integer> conceptList=rs.getConcepts();
		int i=conceptList.get(0);
		String error=rs.addConcept(i);
		Assert.assertNotSame(error,"");
	}

	/**
	 * @verifies {@link RegaService#deleteRegaData(RegaData)}
	 * test = should delete the given RegaData Object
	 */
	@Test
	public void deleteRegaData_shouldDeleteTheGivenRegaDataObject()
			throws Exception {
		RegaService rs=Context.getService(RegaService.class);
		List<RegaData> beforeDeleteList=rs.getRegaData();
		RegaData regaData=beforeDeleteList.get(0);
		rs.deleteRegaData(regaData);
		List<RegaData> afterDeleteList=rs.getRegaData();
		boolean deleted=true;
		for(RegaData rd:afterDeleteList)
		{
			if(rd.getPatientDataId().equals(beforeDeleteList.get(0).getPatientDataId()))
				deleted=false;
		}
		Assert.assertTrue(deleted);
	}

	/**
	 * @verifies {@link RegaService#getConcepts()}
	 * test = should return the list of existing concepts in the database
	 */
	@Test
	public void getConcepts_shouldReturnTheListOfExistingConceptsInTheDatabase()
			throws Exception {
		RegaService rs=Context.getService(RegaService.class);
		List<Integer> conceptList=rs.getConcepts();
		Assert.assertEquals(2, conceptList.size());
	}

	/**
	 * @verifies {@link RegaService#getRegaData()}
	 * test = should return the list of RegaDatas in the database
	 */
	@Test
	public void getRegaData_shouldReturnTheListOfRegaDatasInTheDatabase()
			throws Exception {
		RegaService rs=Context.getService(RegaService.class);
		List<RegaData> list=rs.getRegaData();
		Assert.assertEquals(2, list.size());
		
	}

	/**
	 * @verifies {@link RegaService#saveRegaData(String,String)}
	 * test = should save RegaData Object To the database
	 */
	@Test
	public void saveRegaData_shouldSaveRegaDataObjectToTheDatabase()
			throws Exception {
		RegaData regaData=new RegaData();
		Date d=new Date();
		regaData.setPatientId(234667);
		regaData.setDateCreated(d);
		regaData.setFullExport(true);
		RegaService rs=Context.getService(RegaService.class);
		rs.saveRegaData(regaData.getPatientId()+"", null);
		List<RegaData> list=rs.getRegaData();
		boolean exists=false;
		for(RegaData rd:list)
		{
			if(rd.getPatientId().equals(234667))
				exists=true;
		}
		Assert.assertTrue(exists);
	}
}