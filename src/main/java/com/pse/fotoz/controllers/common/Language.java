package com.pse.fotoz.controllers.common;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller managing the setting and changing of the user's display language.
 * @author Robert
 */
@Controller
@RequestMapping("/lang")
public class Language {
 
    /**
     * Sets the display language to the given language or dutch if no supported
     * language was given.
     * @param lang The pathvariable representing the language "en" or "nl"
     * @param request The HttpServletRequest requesting this URI.
     * @return A redirect command to the previous page (provided as paramater
     * in the request).
     */
    @RequestMapping(value = "/{lang}")
    public String setLanguage(@PathVariable("lang") String lang, 
            HttpServletRequest request) {
        request.getSession().setAttribute("lang", lang);

        return "redirect:" + request.getParameter("redirect");
    }
}