/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.controllers.customers;

import com.pse.fotoz.dbal.entities.CustomerAccount;
import com.pse.fotoz.helpers.mav.ModelAndViewBuilder;
import com.pse.fotoz.helpers.users.Users;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author 310054544
 */
@Controller
@RequestMapping("/")
public class CustomerHome {
    
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView loadLoginScreen(HttpServletRequest request, 
            HttpServletResponse response) {
        ModelAndView mav = ModelAndViewBuilder.empty().                
                withProperties(request).
                withCookies(request, response).
                build();
        
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         String name = auth.getName();
        
         if(!name.equals("anonymousUser"))
         {
             mav.addObject("username", name);
         }
                       

        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String redirect = request.getRequestURL().toString();
        });            

        mav.setViewName("customers/home/index.twig");

        return mav;
    }
    
}
