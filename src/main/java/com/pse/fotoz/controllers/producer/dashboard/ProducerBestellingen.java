package com.pse.fotoz.controllers.producer.dashboard;

import com.pse.fotoz.domain.entities.Order;
import com.pse.fotoz.domain.entities.OrderEntry;
import com.pse.fotoz.domain.entities.Picture;
import com.pse.fotoz.helpers.ModelAndViewBuilder;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller handling the display of the main dashboard for producers.
 * @author Robert
 */
@Controller
@RequestMapping("/producer/dashboard/orders")
public class ProducerBestellingen {

    /**
     * Displays the main dashboard for producers.
     * @param request
     * @return 
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView displayOrders(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        
         List<Order> orders = HibernateEntityHelper.all(Order.class);
         
         List list = new ArrayList(orders);
         
         mav.addObject("orders", orders);
         mav.addObject("list", list);
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/orders";
            public String redirect = request.getRequestURL().toString();
        });
        mav.setViewName("producer/dashboard/orders.twig");

        return mav;
    }
    
        @RequestMapping(method = RequestMethod.GET, value = "/paid")
    public ModelAndView displayOrdersPaid(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        
         List<Order> orders = HibernateEntityHelper.all(Order.class);
     
         mav.addObject("orders", orders);
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/orders";
            public String redirect = request.getRequestURL().toString();
        });
        mav.setViewName("producer/dashboard/orderspaid.twig");

        return mav;
    }
    
            @RequestMapping(method = RequestMethod.GET, value = "/unpaid")
    public ModelAndView displayOrdersUnPaid(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        
         List<Order> orders = HibernateEntityHelper.all(Order.class);

         mav.addObject("orders", orders);
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/orders";
            public String redirect = request.getRequestURL().toString();
        });
        mav.setViewName("producer/dashboard/ordersunpaid.twig");

        return mav;
    }
    
}
