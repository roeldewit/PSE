package com.pse.fotoz.controllers.customers;

import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.domain.entities.Shop;
import com.pse.fotoz.helpers.ModelAndViewBuilder;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/customers/login")
public class CustomerLogin {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView loadLoginScreen(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();

        if (!name.equals("anonymousUser")) {
            mav.addObject("username", name);
        }

        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/customers/login";
            public String redirect = request.getRequestURL().toString();
        });

        mav.setViewName("customers/home/index.twig");

        return mav;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/fotos")
    public ModelAndView displayShops(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();

        List<Shop> shops = HibernateEntityHelper.all(Shop.class);

        mav.addObject("shops", shops);
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/customers/login/fotos";
            public String redirect = request.getRequestURL().toString();
        });
        mav.setViewName("customers/login/fotos.twig");

        return mav;
    }

}
