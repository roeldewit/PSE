package com.pse.fotoz.auth;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

/**
 *
 * @author Gijs
 */
public class RedirectAuthenticationSuccesHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication) throws
            IOException, ServletException {
        if (request.getParameter("redirect") != null) {
            redirectStrategy.sendRedirect(request, response,
                    request.getParameter("redirect"));
        } else if (authentication.getAuthorities().iterator().next().getAuthority().equals("ROLE_ADMIN")) {
            
           redirectStrategy.sendRedirect(request, response, "/app/producer/dashboard");
            
        } else if (authentication.getAuthorities().iterator().next().getAuthority().equals("ROLE_PHOTOGRAPHER")) {
            redirectStrategy.sendRedirect(request, response, "/app/photographers/shop/");
        }
        else {
            redirectStrategy.sendRedirect(request, response, "/app/");
        }
    }

    @Override
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    @Override
    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

}
