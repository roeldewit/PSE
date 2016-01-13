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

        cart.getOrder().getEntries().forEach(e
                -> e.getType().setStock(e.getType().getStock() - e.getAmount()));

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
            calculateAll();
        }

        /**
         * Runs all methods to set all properties. Calculates necessary
         * parameters for these methods.
         */
        public void calculateAll() {

            //calculate properties of product type.
            calculateProductProperties(400, 300,
                    entry.getType().getWidth(),
                    entry.getType().getHeight());

            //get size of picture after being cropped.
            double cropWidth = entry.getOptions().getOffsetXStop() == 0
                    ? entry.getPicture().getWidth()
                    : entry.getOptions().getOffsetXStop()
                    - entry.getOptions().getOffsetXStart();

            double cropHeight = entry.getOptions().getOffsetYStop() == 0
                    ? entry.getPicture().getHeight()
                    : entry.getOptions().getOffsetYStop()
                    - entry.getOptions().getOffsetYStart();

            //get size of overlay on product type picture
            int overlayXStart = entry.getType().getOverlayXStart();
            int overlayXStop = entry.getType().getOverlayXStop();
            int overlayYStart = entry.getType().getOverlayYStart();
            int overlayYStop = entry.getType().getOverlayYStop();

            //get coordinates of necessary picture cropping. 
            //If not cropped: coordinates are that of actual size of picture.
            double offsetXStart = entry.getOptions().getOffsetXStart();
            double offsetXStop = entry.getOptions().getOffsetXStop() == 0
                    ? cropWidth
                    : entry.getOptions().getOffsetXStop();
            double offsetYStart = entry.getOptions().getOffsetYStart();
            double offsetYStop = entry.getOptions().getOffsetYStop() == 0
                    ? cropHeight
                    : entry.getOptions().getOffsetYStop();

            //calculate necessary resizing of cropped picture to fit perfectly in
            //overlay of product type picture.
            double resize = this.calculatePictureResize(cropWidth, cropHeight,
                    overlayXStart, overlayXStop,
                    overlayYStart, overlayYStop,
                    productResize);

            //calculate picture properties
            this.calculatePictureProperties(cropWidth, cropHeight,
                    resize,
                    offsetXStart, offsetXStop,
                    offsetYStart, offsetYStop,
                    overlayXStart, overlayXStop,
                    overlayYStart, overlayYStop,
                    productResize);

            //set chosen color;
            setColor();
        }

        /**
         * Calculates width, height, left outline and top outline of product
         * picture in pixels. Picture of product type will be shown centered in
         * div with maximal size.
         *
         * @param divWidth width of div picture will be shown in
         * @param divHeight height of div picture will be shown in
         * @param actualWidth actual width of picture of product type
         * @param actualHeight actual height of picture of product type
         */
        public void calculateProductProperties(int divWidth, int divHeight,
                int actualWidth, int actualHeight) {

            //calculate necessary resizing for picture to fit in div
            productResize = 1;
            if (actualWidth > divWidth || actualHeight > divHeight) {
                if (actualWidth / actualHeight > divWidth / divHeight) {
                    productResize = (double) divWidth / (double) actualWidth;
                } else {
                    productResize = (double) divHeight / (double) actualHeight;
                }
            }

            //calculate new product picture sizes
            productWidth = (int) Math.round(actualWidth * productResize);
            productHeight = (int) Math.round(actualHeight * productResize);

            //calculate left and top outline. picture must be centered in div.
            productTop = divHeight == productHeight
                    ? 0
                    : (divHeight - productHeight) / 2;

            productLeft = divWidth == productWidth
                    ? 0
                    : (divWidth - productWidth) / 2;
        }

        /**
         * Calculates the necessary resizing of the picture in order to fit
         * inside the overlay of the product type picture.
         *
         * @param cropWidth width of image after cropping
         * @param cropHeight height of image after cropping
         * @param overlayXStart x coordinate of start of picture overlay
         * @param overlayXStop x coordinate of stop of picture overlay
         * @param overlayYStart y coordinate of start of picture overlay
         * @param overlayYStop y coordinate of stop of picture overlay
         * @param productResize resizing of the product type picture in
         * percentage
         * @return necessary resizing in percentage
         */
        public double calculatePictureResize(double cropWidth,
                double cropHeight, int overlayXStart, int overlayXStop,
                int overlayYStart, int overlayYStop, double productResize) {

            double resize = 1;
            double overlayWidth = overlayXStop * productResize
                    - overlayXStart * productResize;
            double overlayHeight = overlayYStop * productResize
                    - overlayYStart * productResize;

            if (cropWidth > overlayWidth || cropHeight > overlayHeight) {
                resize = cropWidth / cropHeight > overlayWidth / overlayHeight
                        ? overlayWidth / cropWidth
                        : overlayHeight / cropHeight;
            }

            return resize;
        }

        /**
         * Calculates width, height, left outline, top outline and cropping of
         * picture in pixels. Picture will be shown centered in product type
         * overlay with maximal size.
         *
         * @param cropWidth width of cropped picture
         * @param cropHeight height of cropped picture
         * @param resize necessary resize of picture
         * @param offsetXStart actual x coordinate of start of cropping
         * @param offsetXStop actual x coordinate of stop of cropping
         * @param offsetYStart actual y coordinate of start of cropping
         * @param offsetYStop actual y coordinate of stop of cropping
         * @param overlayXStart actual x coordinate of start of picture overlay
         * @param overlayXStop actual x coordinate of stop of picture overlay
         * @param overlayYStart actual y coordinate of start of picture overlay
         * @param overlayYStop actual y coordinate of stop of picture overlay
         * @param productResize resizing of product type picture in which the
         * picture will be shown
         */
        public void calculatePictureProperties(double cropWidth,
                double cropHeight, double resize, double offsetXStart,
                double offsetXStop, double offsetYStart, double offsetYStop,
                int overlayXStart, int overlayXStop, int overlayYStart,
                int overlayYStop, double productResize) {

            //calculate new picture size
            pictureWidth = entry.getPicture().getWidth() * resize;
            pictureHeight = entry.getPicture().getHeight() * resize;

            //calculate necessary cropping of each side
            clipTop = offsetYStart * resize;
            clipRight = offsetXStop * resize;
            clipBottom = offsetYStop * resize;
            clipLeft = offsetXStart * resize;

            //calculate left and top outline. Picture must be centered in overlay.
            double overlayXStartN = overlayXStart * productResize;
            double overlayXStopN = overlayXStop * productResize;
            double overlayYStartN = overlayYStart * productResize;
            double overlayYStopN = overlayYStop * productResize;

            pictureTop = productTop + overlayYStartN
                    + (overlayYStopN - overlayYStartN - pictureHeight)
                    / 2
                    + (pictureHeight - clipTop - clipBottom)
                    / 2;

            pictureLeft = productLeft + overlayXStartN
                    + (overlayXStopN - overlayXStartN - pictureWidth)
                    / 2
                    + (pictureWidth - clipRight - clipLeft)
                    / 2;
        }

        public void setColor() {
            color = entry.getOptions().getColor().name();
        }

        public OrderEntry getEntry() {
            return entry;
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
