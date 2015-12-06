package com.pse.fotoz.helpers.forms;

import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.persistence.HibernateException;
import com.pse.fotoz.domain.entities.Customer;
import com.pse.fotoz.domain.entities.CustomerAccount;
import com.pse.fotoz.domain.entities.Photographer;
import com.pse.fotoz.domain.entities.Picture;
import com.pse.fotoz.domain.entities.PictureSession;
import com.pse.fotoz.domain.entities.ProductType;
import com.pse.fotoz.domain.entities.Shop;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import java.util.stream.Stream;

/**
 * Facade abstracting the handling of common user actions.
 * @author Robert
 */
public class PersistenceFacade {
    
    /**
     * Approves a specific picture.
     * @param pictureId The identity of the picture
     * @throws HibernateException If an error occured attempting to persist the
     * modified entity
     * @throws IllegalArgumentException If no picture with given identity
     * exists.
     */
    public static void approvePicture(int pictureId) throws
            HibernateException, IllegalArgumentException {
        Optional<Picture> picture = HibernateEntityHelper.
                byId(Picture.class, pictureId);

        if (!picture.isPresent()) {
            throw new IllegalArgumentException("Given id does not match any "
                    + "picture.");
        } else {
            picture.get().setApproved(Picture.Approved.YES);
            picture.get().persist();
        }
    }

    /**
     * Rejects a specific picture.
     * @param pictureId The identity of the picture
     * @throws HibernateException If an error occured attempting to persist the
     * modified entity
     * @throws IllegalArgumentException If no picture with given identity
     * exists.
     */
    public static void rejectPicture(int pictureId) throws
            HibernateException, IllegalArgumentException {
        Optional<Picture> picture = HibernateEntityHelper.
                byId(Picture.class, pictureId);

        if (!picture.isPresent()) {
            throw new IllegalArgumentException("Given id does not match any "
                    + "picture.");
        } else {
            picture.get().setApproved(Picture.Approved.NO);
            picture.get().persist();
        }
    }
    
     public static void changePicturePrice(int pictureId, BigDecimal price) throws
            HibernateException, IllegalArgumentException {
        Optional<Picture> picture
                = HibernateEntityHelper.byId(Picture.class, pictureId);

        if (!picture.isPresent()) {
            throw new IllegalArgumentException("Given id does not match any "
                    + "picture.");
        } else {
            picture.get().setPrice(price);
            picture.get().persist();
        }
    }

    /**
     * Adds a new shop to the system.
     *
     * @param login Login of the shop
     * @param password Password of the shop
     * @param name Name of the shop's owner
     * @param address Address of the shop's owner
     * @param city City of the shop's owner
     * @param email Email address of the shop's owner
     * @param phone Phone number of the shop's owner
     * @throws HibernateException If a persistence error occured regardless of a
     * correct input.
     */
    public static void addShop(String login, String password,
            String name, String address, String city, String email,
            String phone)
            throws HibernateException {

        Photographer phtgrpr = new Photographer();
        Shop shop = new Shop();

        phtgrpr.setAddress(address);
        phtgrpr.setCity(city);
        phtgrpr.setEmail(email);
        phtgrpr.setName(name);
        phtgrpr.setPhone(phone);

        shop.setLogin(login);
        shop.setPassword(password);
          
        phtgrpr.persist();
        shop.setPhotographer(phtgrpr);
        shop.persist();
    }
    
    /**
     * Adds a customer to the system.
     *
     * @param name Name of the shop's owner
     * @param address Address of the shop's owner
     * @param city City of the shop's owner
     * @param email Email address of the shop's owner
     * @param phone Phone number of the shop's owner
     * @param login Login of the shop
     * @param password Password of the shop
     * @throws HibernateException If a persistence error occured regardless of a
     * correct input.
     */
    public static void addCustomer(String login, String password,
            String name, String address, String city, String email,
            String phone)
            throws HibernateException {

        Customer cus = new Customer();
        CustomerAccount account = new CustomerAccount();

        cus.setName(name);
        cus.setAddress(address);
        cus.setCity(city);
        cus.setPhone(phone);
        cus.setEmail(email);

        account.setLogin(login);
        account.setPasswordHash(password);
        
        cus.persist();
        account.setCustomer(cus);
        account.persist();
    }
    
    /**
     * Adds a Product Type to the system
     * 
     * @param name name of Product Type
     * @param description description of Product Type
     * @param price price excl. VAT of Product Type
     * @param stock currect stock of Product Type
     * @param filename filename of uploaded picture showing Product Type
     * @throws HibernateException If a persistence error occured regardless of a
     * correct input.
     */
    public static void addProductType(String name, String description,
            BigDecimal price, int stock, String filename)
            throws HibernateException {

            ProductType pt = new ProductType();
            pt.setName(name);
            pt.setDescription(description);
            pt.setPrice(price);
            pt.setStock(stock);
            pt.setFilename(filename);

            pt.persist();
    }    
    
    /**
     * Adds a Picture Session to the system
     * 
     * @param shop Shop owning this picture session
     * @param code unique code of picture session
     * @param title title of picture session
     * @param description description of picture session
     * @param isPublic wether the session is public or not
     * @throws HibernateException If a persistence error occured regardless of a
     * correct input.
     */
    public static void addPictureSession(Shop shop, String code, 
            String title, String description, boolean isPublic)
            throws HibernateException {

            PictureSession ps = new PictureSession();
            ps.setShop(shop);
            ps.setCode(code);
            ps.setTitle(title);
            ps.setDescription(description);
            ps.setIsPublic(isPublic);
            
            ps.persist();
    }    
    
    /**
     * Adds a picturesession to the permitted sessions of a customer. 
     * If the picturesession already is in permitted sessions of customer 
     * nothing happens.
     * 
     * @pre Customer is logged in, session with code exists.
     * @param account
     * @param code
     * @throws com.pse.fotoz.persistence.HibernateException
     * @throws NoSuchElementException when picturesession 
     * with corresponding code doesn't exist
     * 
     */
    public static void addPermittedSession(String code, CustomerAccount account)
            throws HibernateException, NoSuchElementException {

        PictureSession session = HibernateEntityHelper.
                find(PictureSession.class, "code", code).stream().
                findAny().
                orElseThrow(() -> new NoSuchElementException("Picture session "
                                + "does not exist."));

        Set<PictureSession> sessions = account.getPermittedSessions();
        if(sessions.stream().noneMatch(s -> s.getCode().equals(code))){           
            account.setPermittedSessions(Stream.concat(
                    sessions.stream(),
                    Stream.of(session)).collect((toSet())));
            account.persist();
        }
        
    }
    
}
