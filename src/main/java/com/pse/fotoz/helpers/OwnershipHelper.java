/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.helpers;

import com.pse.fotoz.domain.entities.Photographer;
import com.pse.fotoz.domain.entities.Picture;
import com.pse.fotoz.domain.entities.PictureSession;
import com.pse.fotoz.domain.entities.Shop;

/**
 *
 * @author René van de Vorst
 */
public class OwnershipHelper {
    
    /**
     * Checks ownership of the shop
     * @param photographer 
     * @param shop
     * @return true if the photographer owns the shop
     */
    public static boolean doesPhotographerOwnShop(Photographer photographer, Shop shop){
        return photographer.doesPhotographerOwnShop(shop);
    }
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
     * Checks if the photographer owns the session and thus the shop the session belongs to(probably not needed in hindsight)
     * @param photographer
     * @param shop
     * @param session
     * @return true if the photographer owns the shop and if that shop owns the session
     */
    public static boolean doesPhotographerOwnShopAndSession(Photographer photographer, Shop shop, PictureSession session){
        return (doesPhotographerOwnShop(photographer, shop) && doesShopOwnPictureSession(shop, session));
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
