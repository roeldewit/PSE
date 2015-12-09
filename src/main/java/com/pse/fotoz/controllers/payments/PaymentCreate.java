package com.pse.fotoz.controllers.payments;

import com.pse.fotoz.domain.entities.Order;
import com.pse.fotoz.payments.PaymentFacade;
import com.pse.fotoz.payments.domain.PaymentRequest;
import com.pse.fotoz.payments.domain.PaymentRequest.Locale;
import com.pse.fotoz.payments.domain.PaymentResponse;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.persistence.HibernateException;
import com.pse.fotoz.properties.CustomLocaleResolver;
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
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class PaymentCreate {

    @RequestMapping(method = RequestMethod.GET, path = "/payment/pay/{orderId}")
    public RedirectView createPayment(HttpServletRequest request,
            HttpServletResponse response, @PathVariable String orderId) {

        Optional<Order> orderOpt = HibernateEntityHelper.
                byId(Order.class, Integer.parseInt(orderId));

        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            PaymentRequest pmRequest = new PaymentRequest();
            Double totalAmount = order.getPriceSum();
            if (totalAmount > 0) {
                pmRequest.setAmount(totalAmount);
                pmRequest.setDescription("Fotoz order " + orderId + ". Bedankt voor uw bestelling!");
                pmRequest.setRedirectUrl(getURLWithContextPath(request) + "/payment/done/" + orderId);

                CustomLocaleResolver clr = new CustomLocaleResolver();
                java.util.Locale loc = clr.resolveLocale(request);
                
                if ("nl".equals(loc.getLanguage())) {
                    pmRequest.setLocale(Locale.NETHERLANDS);
                } else {
                    pmRequest.setLocale(Locale.ENGLAND);
                }

                PaymentFacade pmf = new PaymentFacade();

                try {
                    Optional<PaymentResponse> pmResponse = pmf.CreatePayment(pmRequest);

                    if (pmResponse.isPresent()) {
                        order.setMolliePaymentCreatedDate(pmResponse.get().getCreatedDatetime());
                        order.setMolliePaymentID(pmResponse.get().getId());
                        //order.setMolliePaymentMethod(pmResponse.get().getMethod());
                        order.setStatus(Order.OrderStatus.PLACED);
                        order.setMolliePaymentStatus(pmResponse.get().getStatus());

                        order.persist();//HibernateException: Illegal attempt to associate a collection with two open sessions
                        return new RedirectView(pmResponse.get().getLinks().getPaymentUrl());
                    }
                } catch (RestClientException | HibernateException ex) {
                    Logger.getLogger(PaymentCreate.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        return new RedirectView(
                getURLWithContextPath(request) + "/");
    }

    public static String getURLWithContextPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getServletPath();
    }
}
