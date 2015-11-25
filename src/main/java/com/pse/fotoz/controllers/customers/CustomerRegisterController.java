/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.controllers.customers;

import com.pse.fotoz.controllers.producer.dashboard.ProducerShopsController;
import com.pse.fotoz.dbal.HibernateEntityHelper;
import com.pse.fotoz.dbal.HibernateException;
import com.pse.fotoz.dbal.entities.Customer;
import com.pse.fotoz.dbal.entities.CustomerAccount;
import com.pse.fotoz.helpers.forms.PersistenceFacade;
import com.pse.fotoz.helpers.mav.ModelAndViewBuilder;
import com.pse.fotoz.properties.LocaleUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author 310054544
 */
@Controller
@RequestMapping("/customers/register")
public class CustomerRegisterController {
    
        /**
     * Displays a form to add new customer to the system to the customer.
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView provideNewShopForm(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();

        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/customers/login/register";
            public String redirect = request.getRequestURL().toString();
        });
        mav.setViewName("customers/login/register.twig");

        return mav;
    }
    
    /**
     * Handles a request to register a new customer.
     *
     * @param newCustomerAcc the new Customer Account
     * @param resultCustomerAcc result of new Customer Account validation
     * @param newCustomer the new Customer
     * @param resultCustomer result of new Customer validation
     * @param request http request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView handleNewCustomerForm(
            @ModelAttribute(value = "newCustomerAcc") 
            @Valid CustomerAccount newCustomerAcc,
            BindingResult resultCustomerAcc,
            @ModelAttribute(value = "newCustomer") @Valid Customer newCustomer,
            BindingResult resultCustomer,
            HttpServletRequest request) {

        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                build();

        List<String> errors = new ArrayList<>();

        for (FieldError error : resultCustomerAcc.getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }

        for (FieldError error : resultCustomer.getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }

        //Check of chosen login is unique
        String login = request.getParameter("login");
        if (!HibernateEntityHelper.find(CustomerAccount.class, "login", login)
                .isEmpty()) {
            errors.add(LocaleUtil.getProperties(request).
                    get("ERROR_CUSTOMER_NEWACCOUNT_LOGINALREADYEXISTS"));
        }

        if (errors.isEmpty()) {

            try {
                String password = request.getParameter("passwordHash");
                String name = request.getParameter("name");
                String address = request.getParameter("address");
                String city = request.getParameter("city");
                String phone = request.getParameter("phone");
                String email = request.getParameter("email");

                PersistenceFacade.addCustomer(login, password, name, address,
                        city, email, phone);
                mav.setViewName("customers/login/customer_new_success.twig");
            } catch (HibernateException ex) {
                Logger.getLogger(ProducerShopsController.class.getName()).
                        log(Level.SEVERE, null, ex);
                errors.add(LocaleUtil.getProperties(request).
                        get("ERROR_INTERNALDATABASEERROR"));
                mav.setViewName("customers/login/register.twig");
            }

        } else {
            mav.setViewName("customers/login/register.twig");
        }

        mav.addObject("errors", errors);

        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/customers/login";
            public String redirect = request.getRequestURL().toString();
        });

        return mav;
    }
    
}
