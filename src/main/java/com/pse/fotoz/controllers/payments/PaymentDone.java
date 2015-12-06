package com.pse.fotoz.controllers.payments;

import com.pse.fotoz.domain.entities.Order;
import com.pse.fotoz.helpers.ModelAndViewBuilder;
import com.pse.fotoz.payments.PaymentFacade;
import com.pse.fotoz.payments.domain.PaymentResponse;
import com.pse.fotoz.payments.domain.PaymentResponse.PaymentStatus;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.persistence.HibernateException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PaymentDone {

    @RequestMapping(method = RequestMethod.GET, path = "/payment/done/{orderId}")
    public ModelAndView createPayment(HttpServletRequest request,
            HttpServletResponse response, @PathVariable String orderId) {

        ModelAndView mav = ModelAndViewBuilder.empty().
                withProperties(request).
                withCookies(request, response).
                build();

       
            Optional<Order> orderOpt = HibernateEntityHelper.
                byId(Order.class, Integer.parseInt(orderId));

        if (orderOpt.isPresent() && orderOpt.get().getMolliePaymentID() != null) {
            Order order = orderOpt.get();

            PaymentFacade pmf = new PaymentFacade();

            try {
                Optional<PaymentResponse> pmResponse = pmf.GetPayment(orderOpt.get().getMolliePaymentID());

                if (pmResponse.isPresent()) {
                    order.setMolliePaymentCreatedDate(pmResponse.get().getCreatedDatetime());
                    order.setMolliePaymentPaidDateTime(pmResponse.get().getPaidDateTime());
                    order.setMolliePaymentID(pmResponse.get().getId());
                    order.setMolliePaymentMethod(pmResponse.get().getMethod());
                    order.setStatus(Order.OrderStatus.PAID);
                    order.setMolliePaymentStatus(pmResponse.get().getStatus());

                    order.persist();
                    if(pmResponse.get().getStatus() == PaymentStatus.PAID 
                            || pmResponse.get().getStatus() == PaymentStatus.PENDING
                            || pmResponse.get().getStatus() == PaymentStatus.OPEN){
                    mav.addObject("success", "success");
            }

                }
            } catch (RestClientException|HibernateException ex) {
                Logger.getLogger(PaymentDone.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        mav.setViewName("customers/payment/paymentDone.twig");

        return mav;

    }

    public static String getURLWithContextPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getServletPath();
    }
}
