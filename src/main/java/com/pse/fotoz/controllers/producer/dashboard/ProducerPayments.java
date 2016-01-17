package com.pse.fotoz.controllers.producer.dashboard;

import com.pse.fotoz.domain.entities.Order;
import com.pse.fotoz.domain.entities.Shop;
import com.pse.fotoz.helpers.MapHelper;
import static com.pse.fotoz.helpers.MapHelper.merge;
import com.pse.fotoz.helpers.ModelAndViewBuilder;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.persistence.HibernateException;
import com.pse.fotoz.persistence.HibernateSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller handling the display of the payments for the producer.
 * @author Tarkan
 */
@Controller
@RequestMapping("/producer/dashboard/payments")
public class ProducerPayments {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView displayAllPayments(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/payments";
            public String redirect = request.getRequestURL().toString();
        });
        
        Map<Shop, Double> payments = HibernateEntityHelper.
                all(Order.class).stream().
                map(Order::getPaymentDuePerShop).
                reduce(merge((d1, d2) -> d1 + d2)).
                orElse(Collections.EMPTY_MAP);
         
        mav.addObject("payments", payments.entrySet());
        
        mav.setViewName("producer/dashboard/payments.twig");

        return mav;
    }
    
    @RequestMapping(method = RequestMethod.GET, 
            value = "/{date-start}/{date-end}")
    public ModelAndView displayPaymentsBetween(HttpServletRequest request, 
            @PathVariable("date-start") String dateStart, 
            @PathVariable("date-end") String dateEnd) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();
        
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard/payments";
            public String redirect = request.getRequestURL().toString();
        });
        
        try {
            Date dateMin = new SimpleDateFormat("dd-MM-YYYY").parse(dateStart);
            Date dateMax = new SimpleDateFormat("dd-MM-YYYY").parse(dateEnd);
            
            Session session = HibernateSession.getInstance().getSession();
            
            session.beginTransaction();
            
            List<Order> orders = HibernateSession.getInstance().getSession().
                    createCriteria(Order.class).
                    add(Restrictions.ge("molliePaymentPaidDateTime", dateMin)).
                    add(Restrictions.lt("molliePaymentPaidDateTime", dateMax)).
                    list();
            
            mav.addObject("orders", orders);
            mav.setViewName("producer/dashboard/payments.twig");
        } catch (HibernateException | ParseException ex) {
            return displayAllPayments(request);
        }
     

        return mav;
    }
}
