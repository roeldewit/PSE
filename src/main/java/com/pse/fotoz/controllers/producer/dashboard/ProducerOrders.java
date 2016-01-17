package com.pse.fotoz.controllers.producer.dashboard;

import com.pse.fotoz.config.ConfigurationManager;
import com.pse.fotoz.domain.entities.Order;
import static com.pse.fotoz.domain.entities.Order.ShippingStatus.NOT_SHIPPED;
import static com.pse.fotoz.domain.entities.Order.ShippingStatus.SHIPPED;
import com.pse.fotoz.helpers.EmailHelper;
import com.pse.fotoz.helpers.ModelAndViewBuilder;
import static com.pse.fotoz.payments.domain.PaymentResponse.PaymentStatus.PAID;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.persistence.HibernateException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
        
        mav.setViewName("producer/dashboard/orders.twig");

        return mav;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/paid")
    public ModelAndView displayOrdersPaid(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        
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
    
    @RequestMapping(method = RequestMethod.GET, value = "/not-shipped-but-paid")
    public ModelAndView displayOrdersNotShippedButPaid(HttpServletRequest 
            request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/orders";
            public String redirect = request.getRequestURL().toString();
        });
        
        List<Order> orders = HibernateEntityHelper.all(Order.class).stream().
                filter(o -> o.getShippingStatus() == NOT_SHIPPED).
                filter(o -> o.getMolliePaymentStatus() == PAID).
                collect(toList());

        mav.addObject("orders", orders);
        mav.addObject("displayCsvExport", true);
        mav.setViewName("producer/dashboard/orders.twig");

        return mav;
    }
    
    /**
     * Displays the details of an order to the user.
     * @param id The identity of the order
     * @param request The request.
     * @return View using producer/dashboard/order_detail.twig.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ModelAndView displayOrderDetail(
            @PathVariable(value = "id") int id, 
            HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/orders";
            public String redirect = request.getRequestURL().toString();
        });
        
        Optional<Order> order = HibernateEntityHelper.byId(Order.class, id);
        
        if (order.isPresent()) {
            mav.addObject("order", order.get());
            mav.addObject("isShipped", 
                    order.get().getShippingStatus() == SHIPPED);
            mav.setViewName("producer/dashboard/order_detail.twig");
            
            return mav;
        } else {
            return new ModelAndView("redirect:/app/producer/dashboard/orders");
        }
    }
    
    @RequestMapping(method = RequestMethod.GET, 
            value = "/not-shipped-but-paid/csv")
    public ModelAndView produceOrdersNotShippedCsvReport(HttpServletResponse 
            response) {
        ModelAndView mav = 
                new ModelAndView("producer/dashboard/orders_csv.twig");
        
        List<Order> orders = HibernateEntityHelper.all(Order.class).stream().
                filter(o -> o.getShippingStatus() == NOT_SHIPPED).
                collect(toList());
        
        mav.addObject("orders", orders);
        
        response.setHeader("Content-disposition", 
                "attachment; filename=export.csv");
        
        return mav;
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/ajax/ship")
    public ResponseEntity<String> setShippingStatus(
            HttpServletRequest request) {
        try {
            String data = request.getReader().lines().
                    reduce("", (s1, s2) -> s1 + s2);

            JSONObject json = new JSONObject(data);

            final int orderId = json.getInt("order_id");
            
            Optional<Order> order = HibernateEntityHelper.
                    byId(Order.class, orderId);
            if (order.isPresent() 
                    && order.get().getShippingStatus() != SHIPPED) {
                order.get().setShippingStatus(SHIPPED);
                
                order.get().persist();

                String email = "<h1>Uw bestelling wordt verzonden</h1>\n" +
                        "\n" +
                        "<p>\n" +
                        "Uw bestelling is verwerkt en wordt zo snel " +
                        "mogelijk verzonden. Wij verwachten een " +
                        "leveringsduur van ten meeste 2 dagen.\n" +
                                "</p>\n";

                String emailAddress = order.get().getAccount().getCustomer().
                        getEmail();

                EmailHelper.fromConfig(
                        new XMLConfiguration("application.cfg.xml")).
                        sendEmailHTML(email, 
                                "Uw bestelling bij Fotoz is verwerkt.", 
                                emailAddress, "info@fotoz.nl");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body("corrupt form data");
            }
        } catch (IOException | JSONException | ConfigurationException | 
                MessagingException | HibernateException |
                IllegalArgumentException ex) {
            Logger.getLogger(ConfigurationManager.class.getName()).
                    log(Level.SEVERE, null, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body("corrupt form data");
        }

        return ResponseEntity.ok().body("ok");
    }
}
