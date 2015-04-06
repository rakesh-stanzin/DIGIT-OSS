package org.egov.pgr.web.controller.complaint.citizen;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.egov.pgr.entity.Complaint;
import org.egov.pgr.entity.ComplaintType;
import org.egov.pgr.web.controller.complaint.GenericComplaintController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/complaint/citizen")
public class CitizenComplaintRegistrationController extends GenericComplaintController {

    @RequestMapping(value = "show-reg-form", method = GET)
    public String showComplaintRegistrationForm(@ModelAttribute final Complaint complaint,Model model) {
    	List<ComplaintType> complaintTypes = complaintTypeService.getFrequentlyFiledComplaints();
    	model.addAttribute("complaintTypes", complaintTypes);
        return "complaint/citizen/registration-form";
    }

    @RequestMapping(value = "anonymous/show-reg-form", method = GET)
    public String showAnonymousComplaintRegistrationForm(@ModelAttribute final Complaint complaint,Model model) {
    	List<ComplaintType> complaintTypes = complaintTypeService.getFrequentlyFiledComplaints();
    	model.addAttribute("complaintTypes", complaintTypes);
        return "complaint/citizen/anonymous-registration-form";
    }
    
    @RequestMapping(value = "register", method = POST)
    public String registerComplaint(@Valid @ModelAttribute final Complaint complaint, final BindingResult resultBinder, 
            final RedirectAttributes redirectAttributes, @RequestParam("files") final MultipartFile[] files) {
        if (resultBinder.hasErrors())
            return "complaint/citizen/registration-form";
        complaint.setSupportDocs(addToFileStore(files));
        complaintService.createComplaint(complaint);
        redirectAttributes.addFlashAttribute("complaint", complaint);
        return "redirect:/reg-success";
    }
    
    @RequestMapping(value = "anonymous/register", method = POST)
    public String registerComplaintAnonymous(@Valid @ModelAttribute final Complaint complaint, final BindingResult resultBinder, 
            final RedirectAttributes redirectAttributes, @RequestParam("files") final MultipartFile[] files) {
        if(StringUtils.isBlank(complaint.getComplainant().getEmail()) && StringUtils.isBlank(complaint.getComplainant().getMobile()))
            resultBinder.rejectValue("complainant.email", "email.or.mobile.ismandatory");
        
        if(StringUtils.isBlank(complaint.getComplainant().getName())) 
            resultBinder.rejectValue("complainant.name", "complainant.name.ismandatory");
        
        if (resultBinder.hasErrors()) 
            return "complaint/citizen/anonymous-registration-form";
         
        complaint.setSupportDocs(addToFileStore(files));
        complaintService.createComplaint(complaint);
        redirectAttributes.addFlashAttribute("complaint", complaint);
        return "redirect:/reg-success";
    }

}