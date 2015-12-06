package com.pse.fotoz.helpers;

import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.domain.entities.CustomerAccount;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Robert
 */
public class UserHelper {
    
    public static Optional<String> currentUsername() {
        String result = SecurityContextHolder.getContext().
                getAuthentication().getName();
        
        return result == null ? Optional.empty() : Optional.of(result);
    }
    
    public static Optional<CustomerAccount> currentUserAccount() {
        if (!currentUsername().isPresent()) {
            return Optional.empty();
        }
        
        return HibernateEntityHelper.find(CustomerAccount.class, "login", 
                currentUsername().get()).stream().
                    findAny();
    }
}
