package com.pse.fotoz.controllers.common;

import com.pse.fotoz.helpers.mav.ModelAndViewBuilder;
import com.pse.fotoz.properties.LocaleUtil;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller handling the login functionality for the producer.
 *
 * @author Robert
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    /**
     * Loads the login screen for producers.
     *
     * @param request
     * @param error
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView loadLoginScreen(HttpServletRequest request,
            @RequestParam(value = "error", required = false) String error) {

        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();

        if (error != null) {
            mav.addObject("error", LocaleUtil.getProperties(request)
                    .get("login_error_wrongcredentials"));
        }

        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String redirect = request.getRequestURL().toString();
        });
        mav.addObject("redirect", request.getParameter("redirect"));

        mav.setViewName("common/login/login.twig");

        return mav;
    }

    // for 403 access denied page
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public ModelAndView accesssDenied(HttpServletRequest request, Principal user) {

        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();

        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String redirect = request.getRequestURL().toString();
        });

        if (request.isUserInRole("ROLE_ADMIN")) {
            mav.addObject("msg", user.getName());
            mav.setViewName("common/login/403AD.twig");
        } else if (request.isUserInRole("ROLE_PHOTOGRAPHER")) {
            mav.addObject("msg", user.getName());
            mav.setViewName("common/login/403PH.twig");
        } else if (request.isUserInRole("ROLE_CUSTOMER")) {
            mav.addObject("msg", user.getName());
            mav.setViewName("common/login/403US.twig");
        }

//        if (user != null) {
//            mav.addObject("msg", "Hi " + user.getName()
//                    + ", you do not have permission to access this page!");
//        } else {
//            mav.addObject("msg",
//                    "You do not have permission to access this page!");
//        }
        return mav;

    }

    /*
     @Issue 
     not yet implemented
     */
    /**
     * Services a login request from the client. NYI
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView serviceLoginRequest() {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("common/login/login.twig");

        mav.addObject("labels", LocaleUtil.getProperties("en"));
        mav.addObject("page", new Object() {
            public String lang = "en";
        });
        mav.addObject("error",
                "The login functionality is not yet implemented.");

        return mav;
    }
}
