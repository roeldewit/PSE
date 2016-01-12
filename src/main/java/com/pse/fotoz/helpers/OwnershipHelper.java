package com.pse.fotoz.helpers;

import com.pse.fotoz.domain.entities.Picture;
import com.pse.fotoz.domain.entities.PictureSession;
import com.pse.fotoz.domain.entities.Shop;

/**
 *
 * @author René van de Vorst
 */
public class OwnershipHelper {
    
    /**
     * Checks ownership of the session
     * @param shop
     * @param session
     * @return true if the shop owns the session
     */
    public static boolean doesShopOwnPictureSession(Shop shop, PictureSession session) {
        return shop.doesShopOwnPictureSession(session);
    }
    /**
     * Checks ownership of the picture
     * @param session
     * @param pic
     * @return true if the picture is in the session
     */
    public static boolean doesPictureSessionOwnPicture(PictureSession session, Picture pic){
        return session.doesPictureSessionOwnPicture(pic);
    }
    /**
     * Checks if the Shop owns the picture and thus the session as well
     * @param s
     * @param ps
     * @param pic
     * @return true if the shop owns the session and if that session owns the picture
     */
    public static boolean doesShopOwnPictureSessionAndPicture(Shop s, PictureSession ps, Picture pic){
        return (doesShopOwnPictureSession(s, ps) && doesPictureSessionOwnPicture(ps, pic));
    }
    
    /**
     * checks ownershio
     * @param shop
     * @param username
     * @return true if logged in user owns this shop
     */
    public static boolean doesUserOwnShop(Shop shop, String username){
        return shop.doesUserOwnShop(username);
    }
    
}
