package com.pse.fotoz.controllers.producer.dashboard;

import com.pse.fotoz.domain.entities.Order;
import com.pse.fotoz.helpers.ModelAndViewBuilder;
import com.pse.fotoz.payments.domain.PaymentResponse;
import static com.pse.fotoz.payments.domain.PaymentResponse.PaymentStatus.PAID;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller handling the display of the orders for the producer.
 * @author Tarkan
 */
@Controller
@RequestMapping("/producer/dashboard/orders")
public class ProducerOrders {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView displayOrders(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/orders";
            public String redirect = request.getRequestURL().toString();
        });
        
        List<Order> orders = HibernateEntityHelper.all(Order.class);
         
        mav.addObject("orders", orders);
        mav.addObject("allorders", request);
        
        mav.setViewName("producer/dashboard/orders.twig");

        return mav;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/paid")
    public ModelAndView displayOrdersPaid(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        
        mav.addObject("paid", request);
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/orders";
            public String redirect = request.getRequestURL().toString();
        });
        
        List<Order> orders = HibernateEntityHelper.all(Order.class).stream().
                filter(o -> o.getMolliePaymentStatus() == PAID).
                collect(toList());
     
        mav.addObject("orders", orders);
        mav.setViewName("producer/dashboard/orders.twig");

        return mav;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/unpaid")
    public ModelAndView displayOrdersNotPaid(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        
        mav.addObject("unpaid", request);
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/orders";
            public String redirect = request.getRequestURL().toString();
        });
        
        List<Order> orders = HibernateEntityHelper.all(Order.class).stream().
                filter(o -> o.getMolliePaymentStatus() != PAID).
                collect(toList());

        mav.addObject("orders", orders);
        mav.setViewName("producer/dashboard/orders.twig");

        return mav;
    }
    
}
