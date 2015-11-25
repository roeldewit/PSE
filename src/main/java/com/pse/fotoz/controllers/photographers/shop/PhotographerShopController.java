package com.pse.fotoz.controllers.photographers.shop;

import com.pse.fotoz.dbal.entities.Shop;
import com.pse.fotoz.helpers.mav.ModelAndViewBuilder;
import com.pse.fotoz.helpers.users.Users;
import com.pse.fotoz.properties.LocaleUtil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("photographers/shop")
public class PhotographerShopController {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView loadLoginScreen(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();
        
        String shopName = Users.currentUsername().orElse("");

        mav.addObject("shopName", shopName);
        mav.addObject("page", new Object() {

            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String redirect = request.getRequestURL().toString();
        });

        mav.setViewName("photographers/shop/index.twig");

        return mav;
    }


    /*
     @Issue 
     not yet implemented
     */
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView serviceLoginRequest() {
        ModelAndView mav = new ModelAndView();

        mav.setViewName("photographers/shop/index.twig");

        mav.addObject("labels", LocaleUtil.getProperties("en"));
        mav.addObject("page", new Object() {
            public String lang = "en";
        });
        mav.addObject("error",
                "The login functionality is not yet implemented.");

        Shop shop = Shop.getShopByLogin(Users.currentUsername().
                orElseThrow(() -> new IllegalStateException("User should be "
                        + "logged in.")));
        
        mav.addObject("shopId", shop.getId());
        
        return mav;
    }
}
