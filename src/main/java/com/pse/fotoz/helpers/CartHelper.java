package com.pse.fotoz.helpers;

import com.pse.fotoz.domain.entities.Cart;
import com.pse.fotoz.domain.entities.Order;
import com.pse.fotoz.domain.entities.OrderEntry;
import com.pse.fotoz.domain.entities.Picture;
import com.pse.fotoz.domain.entities.ProductOption;
import com.pse.fotoz.domain.entities.ProductType;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.persistence.HibernateException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * Helper class to handle Cart manipulations. Note that Carts are not a
 * persistent object and thus the methods provided have no interaction with the
 * database unless specified.
 *
 * @author Robert
 */
public class CartHelper {

    /**
     * Adds an item (order entry) to a cart.
     *
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
        entry.setTotalPrice(amount * (productType.getPrice().doubleValue()
                + picture.getPrice().doubleValue()));

        cart.getOrder().getEntries().add(entry);
    }

    /**
     * Updates the amount for an entry within the order of a cart.
     *
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
        entry.setTotalPrice(amount * (entry.getType().getPrice().doubleValue()
                + entry.getPicture().getPrice().doubleValue()));
    }

    /**
     * Removes an entry from the order of a cart.
     *
     * @param cart The cart.
     * @param entryId The id of the entry.
     * @post No entry with entryId exists in carts order.
     */
    public static void removeItemFromCart(Cart cart, int entryId) {
        cart.getOrder().getEntries().removeIf(e -> e.getId() == entryId);
    }

    /**
     * Persists the order to the database.
     *
     * @pre Cart is not empty.
     * @param cart The cart.
     * @throws HibernateException On database error.
     */
    public static void persistOrder(Cart cart) throws HibernateException {
        cart.getOrder().setAccount(UserHelper.currentCustomerAccount().get());
        cart.getOrder().setShippingStatus(Order.ShippingStatus.NOT_SHIPPED);
        cart.getOrder().getEntries().forEach(e -> e.setId(0));
        cart.getOrder().getEntries().forEach(e -> e.setTotalPrice(
                e.getAmount() * (e.getType().getPrice().doubleValue()
                + e.getPicture().getPrice().doubleValue())));

        cart.getOrder().persist();
    }

    /**
     * Returns the Cart associated with the given request, or adds a new, empty
     * Cart to request session.
     *
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
     *
     * @param request
     * @return
     */
    public static Cart flushCart(HttpServletRequest request) {
        Cart cart = new Cart();

        request.getSession().setAttribute("cart", cart);

        return cart;
    }

    public static List<OrderEntryPreview> getOrderEntriesWithPreview(Cart cart) {
        List<OrderEntryPreview> entries = new ArrayList<>();
        
        cart.getOrder().getEntries().stream().
                forEach(e -> entries.add(new OrderEntryPreview(e)));

        return entries;
    }

    private static final class OrderEntryPreview {
        
        private final int divWidth = 400;
        private final int divHeight = 300;

        private final OrderEntry entry;
        private double productResize;
        private double productWidth;
        private double productHeight;
        private double productTop;
        private double productLeft;
        private double pictureWidth;
        private double pictureHeight;
        private double pictureTop;
        private double pictureLeft;
        private double clipTop;
        private double clipRight;
        private double clipBottom;
        private double clipLeft;
        private String color;

        OrderEntryPreview(OrderEntry entry) {
            this.entry = entry;
            calculateProductProperties();
            calculatePictureProperties();
            setColor();
        }

        public void calculateProductProperties() {

            int actualWidth = entry.getType().getWidth();
            int actualHeight = entry.getType().getHeight();
            productResize = 1;

            if (actualWidth > divWidth || actualHeight > divHeight) {
                if (actualWidth / actualHeight > divWidth / divHeight) {
                    productResize = (double) divWidth / (double) actualWidth;
                } else {
                    productResize = (double)divHeight / (double)actualHeight;
                }
            }
            
            productWidth = (int)Math.round(actualWidth * productResize);
            productHeight = (int)Math.round(actualHeight * productResize);

            productTop = divHeight == productHeight
                    ? 0
                    : (divHeight - productHeight) / 2;

            productLeft = divWidth == productWidth
                    ? 0
                    : (divWidth - productWidth) / 2;
        }

        public void calculatePictureProperties() {
            double cropWidth = entry.getOptions().getOffsetXStop()
                    - entry.getOptions().getOffsetXStart();
            double cropHeight = entry.getOptions().getOffsetYStop()
                    - entry.getOptions().getOffsetYStart();

            if(cropWidth==0){
                cropWidth = entry.getPicture().getWidth();
            }
            
            if(cropHeight==0){
                cropHeight = entry.getPicture().getHeight();
            }
            
            double resize = 1;
            double overlayWidth = Math.round(entry.getType().getOverlayXStop() * productResize
                    - entry.getType().getOverlayXStart() * productResize);
            double overlayHeight = Math.round(entry.getType().getOverlayYStop() * productResize
                    - entry.getType().getOverlayYStart() * productResize);
            
            if (cropWidth > overlayWidth || cropHeight > overlayHeight) {
                if (cropWidth / cropHeight > overlayWidth / overlayHeight) {
                    resize = overlayWidth / cropWidth;
                } else {
                    resize = overlayHeight / cropHeight;
                }
            }

            pictureWidth = entry.getPicture().getWidth() * resize;
            pictureHeight = entry.getPicture().getHeight() * resize;
            
            //TEMPORARY
            double offsetXStart = entry.getOptions().getOffsetXStart();
            double offsetXStop = entry.getOptions().getOffsetXStop();
            double offsetYStart = entry.getOptions().getOffsetYStart();
            double offsetYStop = entry.getOptions().getOffsetYStop();
            
            if(offsetXStop == 0){
                offsetXStop = cropWidth;
            }
            if(offsetYStop==0){
                offsetYStop = cropHeight;
            } 
            
            clipTop = offsetYStart * resize;
            clipRight = offsetXStop * resize;
            clipBottom = offsetYStop * resize;
            clipLeft = offsetXStart * resize;
             
            double overlayXStart = Math.round(entry.getType().getOverlayXStart() * productResize);
            double overlayXStop = Math.round(entry.getType().getOverlayXStop() * productResize);
            double overlayYStart = Math.round(entry.getType().getOverlayYStart() * productResize);
            double overlayYStop = Math.round(entry.getType().getOverlayYStop() * productResize);
            
            pictureTop = productTop + overlayYStart 
                    + (overlayYStop-overlayYStart-pictureHeight)
                    / 2
                    + (pictureHeight - clipTop - clipBottom)
                    / 2;
            
            pictureLeft = productLeft + overlayXStart
                    + (overlayXStop - overlayXStart - pictureWidth)
                    / 2
                    + (pictureWidth - clipRight - clipLeft)
                    / 2;
        }
        
        public void setColor(){
            color = entry.getOptions().getColor().name();
        }

        public OrderEntry getEntry() {
            return entry;
        }
        
        public int getDivWidth() {
            return divWidth;
        }

        public int getDivHeight() {
            return divHeight;
        }

        public double getProductResize() {
            return productResize;
        }

        public double getProductWidth() {
            return productWidth;
        }

        public double getProductHeight() {
            return productHeight;
        }

        public double getProductTop() {
            return productTop;
        }

        public double getProductLeft() {
            return productLeft;
        }

        public double getPictureWidth() {
            return pictureWidth;
        }

        public double getPictureHeight() {
            return pictureHeight;
        }

        public double getPictureTop() {
            return pictureTop;
        }

        public double getPictureLeft() {
            return pictureLeft;
        }

        public double getClipTop() {
            return clipTop;
        }

        public double getClipRight() {
            return clipRight;
        }

        public double getClipBottom() {
            return clipBottom;
        }

        public double getClipLeft() {
            return clipLeft;
        }

        public String getColor() {
            return color;
        }
    }
}
