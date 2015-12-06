package com.pse.fotoz.controllers.producer.dashboard;

import com.pse.fotoz.helpers.ModelAndViewBuilder;
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
@RequestMapping("/producer/dashboard")
public class ProducerDashboard {

    /**
     * Displays the main dashboard for producers.
     * @param request
     * @return 
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView displayDashboard(HttpServletRequest request) {
        ModelAndView mav = ModelAndViewBuilder.empty().
                    withProperties(request).
                    build();

        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/producer/dashboard";
            public String redirect = request.getRequestURL().toString();
        });
        mav.setViewName("producer/dashboard/index.twig");

        return mav;
    }
}
