package org.openmrs.module.regadbintegration.advice;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.regadbintegration.RegaData;
import org.openmrs.module.regadbintegration.RegaService;
import org.springframework.aop.AfterReturningAdvice;

public class RegaObsAdvice implements AfterReturningAdvice {

	public void afterReturning(Object returnValue, Method method, Object[] args,Object target) throws Throwable {
		RegaService regaService=Context.getService(RegaService.class);
		if(method.getName().contains("saveObs")){
			Obs obs=(Obs)returnValue;
			boolean exists=false;
			List<RegaData> stagingRegaData=regaService.getRegaData();
			for(RegaData rd:stagingRegaData)
			{
				if(rd.getObsId()!=null){
					if(obs.getId().equals(rd.getObsId()))
						exists=true;
				}
			}
			if(!exists){
				Person person=Context.getPersonService().getPerson(obs.getPersonId());
				boolean isRelated=false;
				Iterator<Integer> conceptIterator=regaService.getConcepts().iterator();
				while (conceptIterator.hasNext())
					if(obs.getConcept().getId().equals(conceptIterator.next())){
						isRelated=true;
						break;
					}


				if(person.getAttribute("RegaExportID")!=null&&isRelated)
					regaService.saveRegaData(obs.getPersonId()+"",obs.getId()+"");
			}
		}

	}
}
