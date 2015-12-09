package com.pse.fotoz.domain.filters;

import com.pse.fotoz.domain.entities.Picture;
import com.pse.fotoz.domain.entities.PictureSession;
import com.pse.fotoz.domain.entities.Shop;
import java.util.function.Predicate;

/**
 * Class prividing filters on shops.
 * @author Robert
 */
public class ShopFilters {
    
    /**
     * A shop is considered visible if it contains at least one picture session
     * with at least one non-hidden picture.
     * @return 
     */
    public static Predicate<Shop> isVisible() {
        Predicate<PictureSession> isPublic = session -> session.isPublic();
        Predicate<PictureSession> hasPublicPicture = session -> 
                session.getPictures().stream().
                        anyMatch(picture -> !picture.isHidden() && 
                                picture.getApproved() == Picture.Approved.YES);
        
        return shop -> shop.getSessions().stream().
                anyMatch(isPublic.and(hasPublicPicture));
    }
}
