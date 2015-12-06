package com.pse.fotoz.helpers;

import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.persistence.HibernateException;
import com.pse.fotoz.domain.entities.Cart;
import com.pse.fotoz.domain.entities.Order;
import com.pse.fotoz.domain.entities.OrderEntry;
import com.pse.fotoz.domain.entities.Picture;
import com.pse.fotoz.domain.entities.ProductOption;
import com.pse.fotoz.domain.entities.ProductType;
import javax.servlet.http.HttpServletRequest;

/**
 * Helper class to handle Cart manipulations.
 * Note that Carts are not a persistent object and thus the methods provided 
 * have no interaction with the database unless specified.
 * @author Robert
 */
public class CartHelper {
    
    /**
     * Adds an item (order entry) to a cart.
     * @param cart The cart the item should be added to.
     * @param pictureId The id of the Picture.
     * @param productTypeId The id of the ProductType.
     * @param amount The amount of items added.
     * @param options The ProductOption to associate with this item.
     * @throws IllegalArgumentException If the Picture or ProductType id does
     * not point to an existing entity or if the amount is non-positive
     */
    public static void addItemToCart(Cart cart, int pictureId, 
            int productTypeId, int amount, ProductOption options) 
            throws IllegalArgumentException {
        if (amount < 1) {
            throw new IllegalArgumentException("Non-positive amount of items "
                    + "to add.");
        }
        
        Picture picture = HibernateEntityHelper.byId(Picture.class, pictureId).
                orElseThrow(() -> new IllegalArgumentException("Picture does "
                        + "not exist."));
        
        ProductType productType = HibernateEntityHelper.byId(ProductType.class, 
                productTypeId).
                orElseThrow(() -> new IllegalArgumentException("Product type "
                        + "does not exist."));
        
        int entryTempId = cart.getOrder().getEntries().stream().
                map(e -> e.getId()).
                max((i1, i2) -> Integer.compare(i1, i2)).
                map(i -> i + 1).
                orElse(0);
        
        OrderEntry entry = new OrderEntry();
        
        entry.setId(entryTempId);
        entry.setOrder(cart.getOrder());
        entry.setPicture(picture);
        entry.setType(productType);
        entry.setOptions(options);        
        entry.setAmount(amount);
        entry.setTotalPrice(amount * ( productType.getPrice().doubleValue() + 
                picture.getPrice().doubleValue() ));
        
        cart.getOrder().getEntries().add(entry);
    }
    
    /**
     * Updates the amount for an entry within the order of a cart.
     * @param cart The cart.
     * @param entryId The id of the entry.
     * @param amount The new amount.
     * @throws IllegalArgumentException if the entry does not exist, or the
     * amount is non-positive.
     * @post Entry with entryId in cart has an amount of amount.
     */
    public static void updateItemAmount(Cart cart, int entryId, int amount) 
            throws IllegalArgumentException {
        if (amount < 1) {
            throw new IllegalArgumentException("Non-positive amount of items "
                    + "to add.");
        }
        
        OrderEntry entry = cart.getOrder().getEntries().stream().
                filter(e -> e.getId() == entryId).
                findAny().
                orElseThrow(() -> new IllegalArgumentException("Entry is not "
                        + "present in cart."));
        
        entry.setAmount(amount);
        entry.setTotalPrice(amount * ( entry.getType().getPrice().doubleValue() + 
                entry.getPicture().getPrice().doubleValue() ));
    }
    
    /**
     * Removes an entry from the order of a cart.
     * @param cart The cart.
     * @param entryId The id of the entry.
     * @post No entry with entryId exists in carts order.
     */
    public static void removeItemFromCart(Cart cart, int entryId) {
       cart.getOrder().getEntries().removeIf(e -> e.getId() == entryId);
    }
    
    /**
     * Persists the order to the database.
     * @pre Cart is not empty.
     * @param cart The cart.
     * @throws HibernateException On database error. 
     */
    public static void persistOrder(Cart cart) throws HibernateException {
        cart.getOrder().setAccount(UserHelper.currentUserAccount().get());
        cart.getOrder().setStatus(Order.OrderStatus.PLACED);
        cart.getOrder().getEntries().forEach(e -> e.setId(0));
        cart.getOrder().getEntries().forEach(e -> e.setTotalPrice(
                e.getAmount() * ( e.getType().getPrice().doubleValue() + 
                e.getPicture().getPrice().doubleValue() )));
        
        cart.getOrder().persist();
    }
    
    /**
     * Returns the Cart associated with the given request, or adds a new, empty 
     * Cart to request session.
     * @param request
     * @return 
     */
    public static Cart getCurrentCart(HttpServletRequest request) {
        Cart cart = new Cart();
        
        try {
            if ((Cart) request.getSession().getAttribute("cart") == null) {
                request.getSession().setAttribute("cart", cart);
            } else {
                cart = (Cart) request.getSession().getAttribute("cart");
            }
        } catch (ClassCastException ex) {
            request.getSession().setAttribute("cart", cart);
        }
        
        return cart;       
    }
    
    /**
     * Resets the cart instance associated with the request.
     * @param request
     * @return 
     */
    public static Cart flushCart(HttpServletRequest request) {
        Cart cart = new Cart();
        
        request.getSession().setAttribute("cart", cart);
        
        return cart;
    }
}
