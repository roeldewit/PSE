package com.pse.fotoz.controllers.producer.dashboard;

import com.pse.fotoz.domain.entities.Order;
import com.pse.fotoz.domain.entities.Shop;
import static com.pse.fotoz.helpers.MapHelper.merge;
import com.pse.fotoz.helpers.ModelAndViewBuilder;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.persistence.HibernateException;
import com.pse.fotoz.persistence.HibernateSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller handling the display of the payments for the producer.
 * @author Robert
 */
@Controller
@RequestMapping("/producer/dashboard/payments")
public class ProducerPayments {

    /**
     * Controller handling the display of the payments for the producer.
     * @author Robert
     * @param request The associated request.
     * @return Display of all payments.
     */
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
    
    /**
     * Controller handling the export of the payments for the producer in csv
     * form.
     * @author Robert
     * @param request The associated request.
     * @param response The associated response.
     * @return Csv export of all payments.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/csv")
    public ModelAndView exportAllPaymentsCsv(HttpServletRequest request, 
            HttpServletResponse response) {
        ModelAndView mav = displayAllPayments(request);
        
        mav.setViewName("producer/dashboard/payments_csv.twig");
        
        response.setHeader("Content-disposition", 
                "attachment; filename=export.csv");
        
        return mav;
    }
    
    /**
     * Controller handling the display of the payments for the producer 
     * filtered by date.
     * @author Robert
     * @param request The associated request.
     * @param dateStart The start date in format 01/01/2001.
     * @param dateEnd The end date in format 01/01/2001.
     * @return Display of all payments within the given timeframe.
     */
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
    
    /**
     * Controller handling the display of the payments for the producer 
     * filtered by date in csv form.
     * @author Robert
     * @param request The associated request.
     * @param dateStart The start date in format 01/01/2001.
     * @param dateEnd The end date in format 01/01/2001.
     * @param response The associated response.
     * @return Csv export of all payments within the given timeframe.
     */
    @RequestMapping(method = RequestMethod.GET, 
            value = "/{date-start}/{date-end}/csv")
    public ModelAndView exportPaymentsBetweenCsv(HttpServletRequest request,  
            @PathVariable("date-start") String dateStart, 
            @PathVariable("date-end") String dateEnd,
            HttpServletResponse response) {
        ModelAndView mav = displayPaymentsBetween(request, dateStart, dateEnd);
        
        mav.setViewName("producer/dashboard/payments_csv.twig");
        
        response.setHeader("Content-disposition", 
                "attachment; filename=export.csv");
        
        return mav;
    }
}
