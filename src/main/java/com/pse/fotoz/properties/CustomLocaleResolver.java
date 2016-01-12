package com.pse.fotoz.properties;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;

/**
 *
 * @author Gijs
 */
public class CustomLocaleResolver implements LocaleResolver {
    
    @Override
    public Locale resolveLocale(HttpServletRequest request) {        
        try {
            String lang = request.getSession().getAttribute("lang").toString();
            return LocaleUtil.getLocale(lang);
        } catch (NullPointerException e) {
            return LocaleUtil.getLocale("nl");
        }
    }

   @Override
    public void setLocale(HttpServletRequest hsr, HttpServletResponse hsr1, 
            Locale locale) {
        throw new UnsupportedOperationException("Locale not able to "
                + "change via set.");
    }
}
