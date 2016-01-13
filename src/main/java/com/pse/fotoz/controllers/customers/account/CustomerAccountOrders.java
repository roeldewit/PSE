package com.pse.fotoz.controllers.customers.account;

import com.pse.fotoz.domain.entities.CustomerAccount;
import com.pse.fotoz.domain.entities.Order;
import com.pse.fotoz.helpers.CartHelper;
import com.pse.fotoz.helpers.ModelAndViewBuilder;
import com.pse.fotoz.helpers.UserHelper;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Robert
 */
@Controller
@RequestMapping("/customers/account/orders")
public class CustomerAccountOrders {

    /**
     * Displays all orders a customer has placed.
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView displayOrders(HttpServletRequest request,
            HttpServletResponse response) {

        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                withCookies(request, response).
                build();

        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/customers/account/orders";
            public String redirect = request.getRequestURL().toString();
        });

        Optional<CustomerAccount> customerOpt = UserHelper.currentCustomerAccount();

        if (customerOpt.isPresent()) {
            CustomerAccount customer = customerOpt.get();
            List<Order> orders = HibernateEntityHelper.all(Order.class).stream()
                    .filter(o -> o.getAccount().getId()==customer.getId())
                    .collect(toList());

            mav.addObject("orders", orders);

            mav.setViewName("customers/account/orders_overview.twig");
        } else {
            mav.setViewName("redirect:/app/login");
        }

        return mav;
    }

    /**
     * Displays requested order.
     *
     * @param request
     * @param response
     * @param id id of order
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ModelAndView displayOrder(HttpServletRequest request,
            HttpServletResponse response, @PathVariable int id) {

        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                withCookies(request, response).
                build();

        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/customers/account/orders";
            public String redirect = request.getRequestURL().toString();
        });

        Optional<Order> orderOpt = HibernateEntityHelper.byId(Order.class, id);
        Optional<CustomerAccount> customerOpt = UserHelper.currentCustomerAccount();

        if (orderOpt.isPresent()
                && customerOpt.isPresent()
                && orderOpt.get().getAccount().getId() == customerOpt.get().getId()) 
        {
            Order order = orderOpt.get();
            mav.addObject("entryPreviews", 
                    CartHelper.getOrderEntriesWithPreview(order.getEntries()));
            mav.addObject("order", order);
            mav.setViewName("customers/account/order.twig");
        } else {
            mav.setViewName("redirect:/app/login");
        }
        
        return mav;
    }

}
