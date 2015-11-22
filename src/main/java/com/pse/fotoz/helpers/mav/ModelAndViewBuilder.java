package com.pse.fotoz.helpers.mav;

import com.pse.fotoz.properties.LocaleUtil;
import java.util.HashMap;
import java.util.Map;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

/**
 * Builder class for populating a ModelAndView with recurring basic attributes.
 *
 * @author Robert
 */
public class ModelAndViewBuilder {

    private final ModelAndView blueprint = new ModelAndView();

    /**
     * Adds properties to ModelAndView
     *
     * @param request The HttpServetRequest to determine properties from
     * @return this
     */
    public ModelAndViewBuilder withProperties(
            HttpServletRequest request) {
        Map<String, String> labels;

        try {
            labels = LocaleUtil.getProperties(
                    request.getSession().getAttribute("lang").toString());
        } catch (IllegalArgumentException | NullPointerException e) {
            request.getSession().setAttribute("lang", "nl");
            labels = LocaleUtil.getProperties(
                    request.getSession().getAttribute("lang").toString());
        }

        blueprint.addObject("labels", labels);

        return this;
    }

    /**
     * Adds cookies to the blueprint.
     *
     * @param request The associated request.
     * @param response The associated response.
     * @return Builder containing cookies.
     */
    public ModelAndViewBuilder withCookies(HttpServletRequest request,
            HttpServletResponse response) {
        
        try{
            Map<String, String> cookies = Stream.of(request.getCookies()).
                    collect(toMap(c -> c.getName(), c -> c.getValue()));
            blueprint.addObject("cookies", cookies);
        } catch (NullPointerException ex){
            blueprint.addObject("cookies", new HashMap<String, String>());
        }

        return this;
    }

    /**
     * Returns the built ModelAndView.
     *
     * @return The result of the building procedure.
     */
    public ModelAndView build() {
        return blueprint;
    }

    /**
     * Gives a blank builder.
     *
     * @return Empty builder.
     */
    public static ModelAndViewBuilder empty() {
        return new ModelAndViewBuilder();
    }
}
