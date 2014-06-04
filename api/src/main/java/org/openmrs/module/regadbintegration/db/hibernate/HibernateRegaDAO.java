package org.openmrs.module.regadbintegration.db.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.regadbintegration.RegaData;
import org.openmrs.module.regadbintegration.db.RegaDAO;

public class HibernateRegaDAO implements RegaDAO{
	protected final Log log = LogFactory.getLog(getClass());
	private SessionFactory sessionFactory;
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void createRegaData(RegaData regaData){
		   try{
		    sessionFactory.getCurrentSession().saveOrUpdate(regaData);
		   }catch(DAOException daoException){
			  log.error("DaoException generated",daoException);
		   } 
	    }
	@SuppressWarnings("unchecked")
	public List<RegaData> getRegaData(){
		return	sessionFactory.getCurrentSession().createCriteria(RegaData.class).list();
	}
	public void deleteRegaData(RegaData regaData) {
		Connection connection = sessionFactory.getCurrentSession().connection();
		Statement s=null;
		try {
			s = connection.createStatement();
			s.executeUpdate("DELETE FROM rega_temp_staging_data where patient_data_id="+regaData.getPatientDataId());
			connection.commit();
		
		}
		catch (SQLException e) {
			log.info("ERROR:cannot delete the record");
		}
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public String addConcept(int conceptId) {
		Connection connection = sessionFactory.getCurrentSession().connection();
		PreparedStatement ps=null;
		String errormsg="";
		boolean isexists=false;
		try{
		isexists=conceptExists(conceptId);
		}
		catch (SQLException e) {
			log.info("Error while checking if concept exists");
			errormsg="Concept cannot be inserted";
			return errormsg;
		}
		if(!isexists){
		try {
			ps = connection.prepareStatement("INSERT INTO rega_related_concepts(concept_id) values(?)");
			ps.setInt(1,conceptId);
			ps.executeUpdate();
			connection.commit();
		}
		catch (SQLException e) {
			log.info("ERROR:Concept cannot be inserted");
		}
		if (ps != null) {
			try {
				ps.close();
			}
			catch (SQLException e) {
				log.error("Error generated while closing statement", e);
			}
		}
		}
		else
		errormsg="Concept already exists";
		
		return errormsg;
	}
	public boolean conceptExists(int conceptId)throws SQLException
	{
		Connection connection = sessionFactory.getCurrentSession().connection();
		boolean isexists=false;
		PreparedStatement ps = null;
				ps = connection.prepareStatement("SELECT * FROM rega_related_concepts WHERE concept_id =?");
				ps.setInt(1,conceptId);
				ps.execute();
				if (ps.getResultSet().next()){
					isexists = true;
				}
				else{
					isexists = false;
				}
			if (ps != null) {
				try {
					ps.close();
				}
				catch (SQLException e) {
					log.error("Error generated while closing statement", e);
				}
			}
		
		return isexists;
		
	}
	public List<Integer> getConcepts() {
		List<Integer> conceptList=new ArrayList<Integer>();
		Connection connection = sessionFactory.getCurrentSession().connection();
		Statement s = null;
		ResultSet rs=null;
		try {
			s = connection.createStatement();
			rs=s.executeQuery("SELECT concept_id FROM rega_related_concepts");
			while (rs.next()){
				conceptList.add(rs.getInt(1));
			}
		}
		catch (SQLException e) {
			log.info("ERROR:while retrieving concepts",e);
		}
		
			if (s != null) {
				try {
					s.close();
				}
				catch (SQLException e) {
					log.error("Error generated while closing statement", e);
				}
			}
		return conceptList;
		
	}

	
}
