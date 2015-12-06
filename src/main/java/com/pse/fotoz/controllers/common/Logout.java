package com.pse.fotoz.controllers.common;

import com.pse.fotoz.helpers.ModelAndViewBuilder;
import com.pse.fotoz.properties.LocaleUtil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/logout")
public class Logout {
 
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView loadLoginScreen(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();

        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String redirect = request.getRequestURL().toString();
        });            

        mav.setViewName("customers/home/index.twig");

        return mav;
    }
}
