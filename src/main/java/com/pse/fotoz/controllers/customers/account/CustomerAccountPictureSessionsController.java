package com.pse.fotoz.controllers.customers.account;

import com.pse.fotoz.dbal.HibernateEntityHelper;
import com.pse.fotoz.dbal.HibernateException;
import com.pse.fotoz.dbal.entities.CustomerAccount;
import com.pse.fotoz.dbal.entities.PictureSession;
import com.pse.fotoz.helpers.forms.PersistenceFacade;
import com.pse.fotoz.helpers.mav.ModelAndViewBuilder;
import com.pse.fotoz.helpers.users.Users;
import com.pse.fotoz.properties.LocaleUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Robert
 */
@Controller
@RequestMapping("/customers/account/")
public class CustomerAccountPictureSessionsController {

    /**
     * Displays all picture sessions a customer is permitted to view
     * 
     * @param request
     * @return 
     */
    @RequestMapping(value = "/sessions", method = RequestMethod.GET)
    public ModelAndView displayPictureSessions(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();

        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/customers/account/sessions";
            public String redirect = request.getRequestURL().toString();
        });

        CustomerAccount user = Users.currentUserAccount().
                orElseThrow(
                        () -> new IllegalStateException("Logged in user not present "
                                + "in database."));

        List<PictureSession> sessions
                = HibernateEntityHelper.all(PictureSession.class).stream().
                filter(s -> s.getPermittedAccounts().stream().
                        anyMatch(a -> a.getId() == user.getId())).
                collect(toList());

        mav.addObject("sessions", sessions);

        mav.setViewName("customers/account/sessions_overview.twig");

        return mav;
    }

    /**
     * Adds a picture session to the sessions a customer is allowed to view
     * 
     * @param request
     * @return 
     */
    @RequestMapping(value = "/sessions", method = RequestMethod.POST)
    public ModelAndView addPictureSession(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();

        CustomerAccount customer = Users.currentUserAccount().
                orElseThrow(
                        () -> new IllegalStateException("Logged in user not "
                                + " present in database."));
        
        //add the picturesession to permitted sessions of customer 
        //and retrieve any errors
        List<String> errors = new ArrayList<>();
        errors = addPermittedSession(
                request, request.getParameter("code"), customer);

        List<PictureSession> sessions
                = HibernateEntityHelper.all(PictureSession.class).stream().
                filter(s -> s.getPermittedAccounts().stream().
                        anyMatch(a -> a.getId() == customer.getId())).
                collect(toList());

        mav.addObject("sessions", sessions);
        mav.addObject("errors", errors);

        mav.setViewName("customers/account/sessions_overview.twig");
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/customers/account/sessions";
            public String redirect = request.getRequestURL().toString();
        });

        return mav;
    }

    /**
     * Adds a picture session to the sessions a customer is allowed to view
     * 
     * @param request
     * @param code unique code of session to be added
     * @param redirectAttributes attributes to be added to redirect view
     * @return 
     */
    @RequestMapping(value = "/sessions/add/{code}", method = RequestMethod.GET)
    public ModelAndView addPictureSessionViaURL(HttpServletRequest request,
            @PathVariable String code,
            RedirectAttributes redirectAttributes) {
        
        CustomerAccount customer = Users.currentUserAccount().
                orElseThrow(() -> new IllegalStateException("Logged in user not "
                                + "present in database."));
        
        redirectAttributes.addFlashAttribute(
                "errors", addPermittedSession(request, code, customer));
        
        return new ModelAndView("redirect:/app/customers/account/sessions/");
    }

    /**
     * Adds a picture session to the sessions a customer is allowed to view
     * 
     * @param request
     * @param code unique code of picture session to be added
     * @param customer customer who will be granted permission to view session
     * @return List of errors occured while adding permitted picture session, 
     * empty when added succesfully
     */
    public List<String> addPermittedSession(HttpServletRequest request, 
            String code,
            CustomerAccount customer) {
        
        List<String> errors = new ArrayList<>();

        try {
            PersistenceFacade.addPictureSessionCustomer(
                    code,
                    customer);
        } catch (NoSuchElementException ex) {
            errors.add(LocaleUtil.getProperties(request)
                    .get("ERROR_PICTURE_SESSION_DOESNT_EXIST"));
        } catch (HibernateException ex) {
            Logger.getLogger(CustomerAccountPictureSessionsController.class.getName())
                    .log(Level.SEVERE, null, ex);
            errors.add(LocaleUtil.getProperties(request)
                    .get("ERROR_INTERNALDATABASEERROR"));
        }
        
        return errors;
    }

}
