package org.openmrs.module.regadbintegration.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.regadbintegration.RegaService;
import org.openmrs.web.WebConstants;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class AddRelatedConceptsFormController extends SimpleFormController {

	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object obj,
            BindException errors) throws Exception {

		Concept concept=new Concept();
		if (StringUtils.hasText(request.getParameter("conceptId"))){
			concept.setId(Integer.valueOf(request.getParameter("conceptId")));
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "conceptId", "error.null");
		return super.processFormSubmission(request,response,concept,errors);
}
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		HttpSession httpSession=request.getSession();
		String conceptId = request.getParameter("conceptId");
		RegaService regaService=Context.getService(RegaService.class);
		String error=regaService.addConcept(Integer.parseInt(conceptId));
		if (!error.equals(""))
			httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, error);
		else
			httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "regadbintegration.Concept.addSuccess");
		return new ModelAndView(new RedirectView(getSuccessView()));
	}

	@Override
	protected Map<String,Object> referenceData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		
		return new HashMap<String, Object>();
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		
		Concept concept = null;
		
		if (Context.isAuthenticated()) {
			ConceptService cs = Context.getConceptService();
			String conceptId = request.getParameter("conceptId");
			if (conceptId != null && conceptId!="") {
				concept = cs.getConcept(Integer.valueOf(conceptId));
				
			}
		}
		if (concept == null)
			concept = new Concept();
		
		return concept;
	}

}
