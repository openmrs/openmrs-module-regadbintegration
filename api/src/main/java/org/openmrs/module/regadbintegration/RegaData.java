package org.openmrs.module.regadbintegration;

import java.io.Serializable;
import java.util.Date;

public class RegaData implements Serializable{
	
public static final long serialVersionUID = 113222233L;
	
	// Fields
	private Integer patientDataId;
	private Integer patientId;
	private Integer ObsId;
	private Boolean fullExport;
	private Date dateCreated;
	public RegaData()
	{
		
	}
	public RegaData(Integer patientDataId,Integer patientId,Integer ObsId,Boolean fullExport,Date dateCreated)
	{
		this.patientDataId=patientDataId;
		this.patientId=patientId;
		this.ObsId=ObsId;
		this.fullExport=fullExport;
		this.dateCreated=dateCreated;
	}
	public Integer getPatientDataId() {
		return patientDataId;
	}
	public void setPatientDataId(Integer patientDataId) {
		this.patientDataId = patientDataId;
	}
	public Integer getPatientId() {
		return patientId;
	}
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	public Integer getObsId() {
		return ObsId;
	}
	public void setObsId(Integer obsId) {
		ObsId = obsId;
	}
	public Boolean getFullExport() {
		return fullExport;
	}
	public void setFullExport(Boolean fullExport) {
		this.fullExport = fullExport;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
}
