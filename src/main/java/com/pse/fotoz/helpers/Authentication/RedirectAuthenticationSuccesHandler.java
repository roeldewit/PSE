package com.pse.fotoz.helpers.Authentication;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 *
 * @author Gijs
 */
public class RedirectAuthenticationSuccesHandler implements 
        AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
            HttpServletResponse response, Authentication authentication) throws 
            IOException, ServletException {
        if (request.getParameter("redirect") != null) {
            redirectStrategy.sendRedirect(request, response, 
                request.getParameter("redirect"));
        } else {
            redirectStrategy.sendRedirect(request, response, "/app");
        }
    }
    
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }
    
    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

}
