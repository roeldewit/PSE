package com.pse.fotoz.controllers.photographers.shop;

import com.pse.fotoz.helpers.ModelAndViewBuilder;
import com.pse.fotoz.helpers.UserHelper;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("photographers/shop")
public class PhotographerShop {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView loadLoginScreen(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();
        
        String shopName = UserHelper.currentUsername().orElse("");

        mav.addObject("shopName", shopName);
        mav.addObject("page", new Object() {

            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String redirect = request.getRequestURL().toString();
        });

        mav.setViewName("photographers/shop/index.twig");

        return mav;
    }
}
