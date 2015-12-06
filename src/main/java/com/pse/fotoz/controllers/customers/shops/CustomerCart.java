package com.pse.fotoz.controllers.customers.shops;

import com.pse.fotoz.persistence.HibernateException;
import com.pse.fotoz.domain.entities.Cart;
import com.pse.fotoz.domain.entities.ProductOption;
import com.pse.fotoz.helpers.CartHelper;
import com.pse.fotoz.helpers.ModelAndViewBuilder;
import com.pse.fotoz.helpers.UserHelper;
import static com.pse.fotoz.properties.LocaleUtil.getProperties;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller handling the display and CRUD operations on the shopping cart.
 * @author Robert
 */
@Controller
@RequestMapping("/customers/cart")
public class CustomerCart {

    /**
     * Displays the contents of their shopping cart to the user.
     * @param request The associated request.
     * @param response The associated response.
     * @return View from "customers/shops/cart.twig".
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView displayCart(HttpServletRequest request, 
            HttpServletResponse response) {
        ModelAndView mav = ModelAndViewBuilder.empty().                
                withProperties(request).
                withCookies(request, response).
                build();
        
        Cart cart = CartHelper.getCurrentCart(request);
        
        cart.getOrder().getEntries().stream().
                forEach(e -> e.getOptions().setLabels(getProperties(request)));
        
        mav.addObject("cart", cart);
        
        mav.addObject("page", new Object() {
            public String lang = request.getSession().
                    getAttribute("lang").toString();
            public String uri = "/customers/cart";
            public String redirect = request.getRequestURL().toString();
        });            

        mav.setViewName("customers/shops/cart.twig");

        return mav;
    }

    /**
     * Ajax endpoint method to add an item to the cart.
     * The following fields are required to process a request:
     * <ul>
     * <li>
     * picture_id : the identity of the picture (Picture::getId).
     * </li>
     * <li>
     * product_type_id : the identity of the product type (ProductType::getId).
     * </li>
     * <li>
     * amount : the amount of the given item are to be added.
     * </li>
     * <li>
     * color : the color option for the item (COLOR | GRAYSCALE | SEPIA).
     * </li>
     * </ul>
     * @param request The associated request.
     * @return 502 if the request cannot be processed, 200 otherwise.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/ajax/add")
    public ResponseEntity<String> addItem(HttpServletRequest request) {
        try {
            String data = request.getReader().lines().
                    reduce("", (s1, s2) -> s1 + s2);

            JSONObject json = new JSONObject(data);

            final int pictureId = json.getInt("picture_id");
            final int productTypeId = json.getInt("product_type_id");
            final int amount = json.getInt("amount");
            final String color = json.getString("color");
            ProductOption options = new ProductOption();            
            options.setColor(ProductOption.ColorOption.valueOf(color));
            
            Cart cart = CartHelper.getCurrentCart(request);

            CartHelper.addItemToCart(cart, pictureId, productTypeId, amount, 
                    options);
        } catch (IOException | JSONException | IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body("corrupt form data");
        }

        return ResponseEntity.ok().body("ok"); 
    }
    
    /**
     * Ajax endpoint method to update the amount of a given item in the cart.
     * The following fields are required to process a request:
     * <ul>
     * <li>
     * entry_id : The identity of the item entry (OrderEntry::getId).
     * </li>
     * <li>
     * amount : The amount to update to (&gt; 0).
     * </li>
     * </ul>
     * @param request The associated request.
     * @return 502 if the request cannot be processed, 200 otherwise.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/ajax/amount")
    public ResponseEntity<String> updateItemAmount(HttpServletRequest request) {
        try {
            String data = request.getReader().lines().
                    reduce("", (s1, s2) -> s1 + s2);

            JSONObject json = new JSONObject(data);

            final int entryId = json.getInt("entry_id");
            final int amount = json.getInt("amount");
            
            Cart cart = CartHelper.getCurrentCart(request);

            CartHelper.updateItemAmount(cart, entryId, amount);
        } catch (IOException | JSONException | IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body("corrupt form data");
        }

        return ResponseEntity.ok().body("ok"); 
    }
    
    /**
     * Ajax endpoint method to remove a given item from the cart.
     * The following fields are required to process a request:
     * <ul>
     * <li>
     * entry_id : The identity of the item entry (OrderEntry::getId).
     * </li>
     * </ul>
     * @param request The associated request.
     * @return 502 if the request cannot be processed, 200 otherwise.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/ajax/remove")
    public ResponseEntity<String> removeItem(HttpServletRequest request) {
        try {
            String data = request.getReader().lines().
                    reduce("", (s1, s2) -> s1 + s2);

            JSONObject json = new JSONObject(data);

            final int entryId = json.getInt("entry_id");
            
            Cart cart = CartHelper.getCurrentCart(request);

            CartHelper.removeItemFromCart(cart, entryId);
        } catch (IOException | JSONException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body("corrupt form data");
        }

        return ResponseEntity.ok().body("ok"); 
    }
    
    /**
     * Ajax endpoint to commit an order.
     * Commits the order to the database, such that one can proceed to payment.
     * Returns the order identity of the created order.
     * @param request The associated request.
     * @return 403 if the user is not logged in, 400 if the cart is empty, 500
     * on an internal error.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/ajax/commit")
    public ResponseEntity<String> commitOrder(HttpServletRequest request) {
        Cart cart = CartHelper.getCurrentCart(request);
        
        if (!UserHelper.currentUserAccount().isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).
                    body("User must be logged in.");
        }
        
        if (cart.getOrder().getEntries().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body("Cart is empty.");
        }
        
        try {
            CartHelper.persistOrder(cart);
            
            int orderId = cart.getOrder().getId();
            
            CartHelper.flushCart(request);            
        
            return ResponseEntity.ok().body("{\"order_id\":" + orderId + "}");
        } catch (HibernateException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body("Database error.");
        }
    }
}
