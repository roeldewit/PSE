package com.pse.fotoz.controllers.producer.dashboard;

import com.pse.fotoz.domain.entities.Picture;
import com.pse.fotoz.helpers.ModelAndViewBuilder;
import com.pse.fotoz.helpers.PersistenceFacade;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.persistence.HibernateException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller displaying and handling of picture submissions to the producer.
 * @author Robert
 */
@Controller
@RequestMapping("/producer/dashboard/submissions")
public class ProducerPictureSubmission {

    /**
     * Displays an overview of submitted pictures to the producer.
     * @param request
     * @return 
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView displaySubmissions(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();

        List<Picture> pictures = HibernateEntityHelper.all(Picture.class).
                stream().
                filter(p -> p.getApproved() == Picture.Approved.PENDING).
                sorted((p1, p2) -> p2.getSubmissionDate().
                        compareTo(p1.getSubmissionDate())).
                collect(Collectors.toList());

        mav.addObject("pictures", pictures);
        
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/submissions";
            public String redirect = request.getRequestURL().toString();
        });

        mav.setViewName("producer/dashboard/submissions.twig"); 

        return mav;
    }
    
        /**
     * Displays an overview of approved pictures to the producer.
     * @param request
     * @return 
     */
    @RequestMapping(method = RequestMethod.GET, value="/approved")
    public ModelAndView displayApproved(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();

        List<Picture> pictures = HibernateEntityHelper.all(Picture.class).
                stream().
                filter(p -> p.getApproved() == Picture.Approved.YES).
                sorted((p1, p2) -> p2.getSubmissionDate().
                        compareTo(p1.getSubmissionDate())).
                collect(Collectors.toList());

        mav.addObject("pictures", pictures);
        mav.addObject("approved", request);
       
        
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/submissions";
            public String redirect = request.getRequestURL().toString();
        });

        mav.setViewName("producer/dashboard/submissions.twig"); 

        return mav;
    }
    
       /**
     * Displays an overview of approved pictures to the producer.
     * @param request
     * @return 
     */
    @RequestMapping(method = RequestMethod.GET, value="/rejected")
    public ModelAndView displayRejected(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();

        List<Picture> pictures = HibernateEntityHelper.all(Picture.class).
                stream().
                filter(p -> p.getApproved() == Picture.Approved.NO).
                sorted((p1, p2) -> p2.getSubmissionDate().
                        compareTo(p1.getSubmissionDate())).
                collect(Collectors.toList());

        mav.addObject("pictures", pictures);
        mav.addObject("rejected", request);
        
        
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/submissions";
            public String redirect = request.getRequestURL().toString();
        });

        mav.setViewName("producer/dashboard/submissions.twig"); 

        return mav;
    }
    
    /**
     * Handles an ajax call to either approve or reject a submitted picture.
     * @param request
     * @return 
     */
    @RequestMapping(method = RequestMethod.POST, value = "/ajax")
    public ResponseEntity<String> handleApproveRejectRequest(
            HttpServletRequest request) {        
            JSONObject json;
            
            try {
                String data = request.getReader().lines().
                        reduce("", (s1, s2) -> s1 + s2);
                
                json = new JSONObject(data);
                json.getString("option");
                json.getString("picture_id");
            } catch (IOException | JSONException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                        body("corrupt form data");
            }
            
            try {                
                switch(json.getString("option")) {
                    case "approve":
                        PersistenceFacade.approvePicture(
                                json.getInt("picture_id"));
                        break;
                    case "reject":
                        PersistenceFacade.rejectPicture(
                                json.getInt("picture_id"));
                        break;
                    default:
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                                body("corrupt form data");
                }
                
                return ResponseEntity.ok().body("ok");                
            } catch (HibernateException | IllegalArgumentException | 
                    NullPointerException | JSONException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                        body("corrupt form data");
            }
    }
}
