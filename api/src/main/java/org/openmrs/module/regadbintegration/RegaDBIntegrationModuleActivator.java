package org.openmrs.module.regadbintegration;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.Activator;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.ModuleException;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.util.OpenmrsConstants;
/**
 * This class contains the logic that is run every time this module
 * is either started or shutdown
 */
//@SuppressWarnings("deprecation")
public class RegaDBIntegrationModuleActivator implements ModuleActivator {

	private Log log = LogFactory.getLog(this.getClass());
	
		/*public void startup() {
		log.info("Starting RegaDBIntegration Module");
		AdministrationService as = Context.getAdministrationService();

		String gp = as.getGlobalProperty("regadbintegration.remoteinstanceaddress"); 
		if ("".equals(gp)) {
			throw new ModuleException("Global property 'regadbintegration.remoteinstanceaddress' must be defined");
		}
		
		gp = as.getGlobalProperty("regadbintegration.regadbusername"); 
		if ("".equals(gp)) {
			throw new ModuleException("Global property 'regadbintegration.regadbusername' must be defined");
		}
		gp = as.getGlobalProperty("regadbintegration.regadbpassword"); 
		if ("".equals(gp)) {
			throw new ModuleException("Global property 'regadbintegration.regadbpassword' must be defined");
		}
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
		PersonService ps=Context.getPersonService();
		if(ps.getPersonAttributeTypeByName("RegaExportID")==null)
		{
		 PersonAttributeType pat=new PersonAttributeType();
		 pat.setName("RegaExportID");
		 pat.setFormat("java.lang.String");
		 pat.setDescription("The ID of the patient in the RegaDB(if already Exported)");
		 ps.savePersonAttributeType(pat);
		}
		else if(ps.getPersonAttributeTypeByName("RegaExportID").isRetired())
		{
			ps.getPersonAttributeTypeByName("RegaExportID").setRetired(false);
			ps.getPersonAttributeTypeByName("RegaExportID").setRetiredBy(null);
			ps.getPersonAttributeTypeByName("RegaExportID").setRetireReason(null);
			ps.getPersonAttributeTypeByName("RegaExportID").setDateRetired(null);
	    }
		SchedulerService ss=Context.getSchedulerService();
		if(ss.getTaskByName("Export Data Task")==null){
			TaskDefinition td = new TaskDefinition();
			td.setName("Export Data Task");
			td.setTaskClass("org.openmrs.module.regadbintegration.RegaTimer");
			td.setStartOnStartup(false);
			Date d=new Date();
			td.setDescription("The timer exports staging data");
			td.setStartTime(d);
			td.setStarted(false);
			td.setRepeatInterval(600L);
			ss.saveTask(td);
		}
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
	}*/
	
	/*public void shutdown() {
		log.info("Shutting down RegaDBIntegration Module");
		SchedulerService ss=Context.getSchedulerService();
		TaskDefinition td;
		if((td=ss.getTaskByName("Export Data Task"))!=null){
			try {
				ss.shutdownTask(td);
			} catch (SchedulerException e) {
				log.info("error on sutting down the task");
			}
			ss.deleteTask(td.getId());
			log.info("Timer Shut Down");
		}
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
		PersonService ps=Context.getPersonService();
		if(ps.getPersonAttributeTypeByName("RegaExportID")!=null)
		{
			ps.retirePersonAttributeType(ps.getPersonAttributeTypeByName("RegaExportID"), "Module Undeployed");
		}
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
	}*/
	@Override
    public void willRefreshContext() {
	    // TODO Auto-generated method stub
	    
    }
	@Override
    public void contextRefreshed() {
	    // TODO Auto-generated method stub
	    
    }
	
    @Override
	/**
	 * @see org.openmrs.module.ModuleActivator#willStart()
	 */
    
	public void willStart() {
	    // TODO Auto-generated method stub
			    
    }
	@SuppressWarnings("deprecation")
    @Override
    public void started() {
	    // TODO Auto-generated method stub
		log.info("Starting RegaDBIntegration Module");
		AdministrationService as = Context.getAdministrationService();

		String gp = as.getGlobalProperty("regadbintegration.remoteinstanceaddress"); 
		if ("".equals(gp)) {
			throw new ModuleException("Global property 'regadbintegration.remoteinstanceaddress' must be defined");
		}
		
		gp = as.getGlobalProperty("regadbintegration.regadbusername"); 
		if ("".equals(gp)) {
			throw new ModuleException("Global property 'regadbintegration.regadbusername' must be defined");
		}
		gp = as.getGlobalProperty("regadbintegration.regadbpassword"); 
		if ("".equals(gp)) {
			throw new ModuleException("Global property 'regadbintegration.regadbpassword' must be defined");
		}
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);
		PersonService ps=Context.getPersonService();
		if(ps.getPersonAttributeTypeByName("RegaExportID")==null)
		{
		 PersonAttributeType pat=new PersonAttributeType();
		 pat.setName("RegaExportID");
		 pat.setFormat("java.lang.String");
		 pat.setDescription("The ID of the patient in the RegaDB(if already Exported)");
		 ps.savePersonAttributeType(pat);
		}
		else if(ps.getPersonAttributeTypeByName("RegaExportID").isRetired())
		{
			ps.getPersonAttributeTypeByName("RegaExportID").setRetired(false);
			ps.getPersonAttributeTypeByName("RegaExportID").setRetiredBy(null);
			ps.getPersonAttributeTypeByName("RegaExportID").setRetireReason(null);
			ps.getPersonAttributeTypeByName("RegaExportID").setDateRetired(null);
	    }
		SchedulerService ss=Context.getSchedulerService();
		if(ss.getTaskByName("Export Data Task")==null){
			TaskDefinition td = new TaskDefinition();
			td.setName("Export Data Task");
			td.setTaskClass("org.openmrs.module.regadbintegration.RegaTimer");
			td.setStartOnStartup(false);
			Date d=new Date();
			td.setDescription("The timer exports staging data");
			td.setStartTime(d);
			td.setStarted(false);
			td.setRepeatInterval(600L);
			ss.saveTask(td);
		}
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER);


    }
	@Override
    public void willStop() {
	    // TODO Auto-generated method stub
	    
    }
	@Override
	/**
	 *  @see org.openmrs.module.ModuleActivator#stopped()
	 */
    public void stopped() {
	    // TODO Auto-generated method stub
		log.info("Shutting down RegaDBIntegration Module");
		SchedulerService ss=Context.getSchedulerService();
		TaskDefinition td;
		if((td=ss.getTaskByName("Export Data Task"))!=null){
			try {
				ss.shutdownTask(td);
			} catch (SchedulerException e) {
				log.info("error on sutting down the task");
			}
			ss.deleteTask(td.getId());
			log.info("Timer Shut Down");
		}
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
		PersonService ps=Context.getPersonService();
		if(ps.getPersonAttributeTypeByName("RegaExportID")!=null)
		{
			ps.retirePersonAttributeType(ps.getPersonAttributeTypeByName("RegaExportID"), "Module Undeployed");
		}
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES);
		Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_PERSON_ATTRIBUTE_TYPES);
    }

	
	
	
}

