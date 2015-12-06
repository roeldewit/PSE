package com.pse.fotoz.domain.filters;

import com.pse.fotoz.domain.entities.CustomerAccount;
import com.pse.fotoz.domain.entities.Picture;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Class providing filters on pictures.
 * @author Robert
 */
public class PictureFilters {

    /**
     * A picture is visible if it is not hidden, has been approved and has a 
     * positive price.
     * @return 
     */
    public static Predicate<Picture> isVisible() {
        return p -> !p.isHidden() && 
                p.getApproved() == Picture.Approved.YES &&
                Optional.ofNullable(p.getPrice()).
                        orElse(BigDecimal.ZERO).doubleValue() > 0;
    }
    
    /**
     * A picture is accessible by a customer account if the customer has
     * access to a session containing said picture.
     * @return 
     */
    public static BiPredicate<Picture, CustomerAccount> isAccessible() {
        return (p, a) -> a.getPermittedSessions().stream().
                anyMatch(s -> s.doesPictureSessionOwnPicture(p));
    }
    
    /**
     * Right curried version of isAccessible().
     * @param c
     * @return 
     */
    public static Predicate<Picture> isAccessibleFor(CustomerAccount c) {
        return p -> isAccessible().test(p, c);
    }
}
