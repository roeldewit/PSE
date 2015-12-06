/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.domain.entities;

import com.pse.fotoz.helpers.PasswordHasher;
import com.pse.fotoz.validators.DoesNotExist;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import com.pse.fotoz.persistence.HibernateException;
import com.pse.fotoz.persistence.HibernateSession;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import org.hibernate.Session;

/**
 *
 * @author René
 */
@Entity
@Table(name = "shops")
public class Shop implements HibernateEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Photographer photographer;

    @Basic
    @Column(name = "login", unique = true)
    @Size(min=1, max=100, message = "{error_size_login}")
    @DoesNotExist(entity=Shop.class, field="login", 
            message="{error_exist_login}")
    private String login;

    @Basic
    @Column(name = "passwordHash")
    @Size(min=4, message = "{error_size_password}")
    private String passwordHash;

    @OneToMany(mappedBy = "shop", fetch = FetchType.EAGER)
    private Set<PictureSession> sessions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the password hash value in the database
     *
     * @param passwordHash Hash value, do not store a plain password
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Sets the password in the database as a hash
     *
     * @param password the plain password that will be stored as a hash
     */
    public void setPassword(String password) {
        try {
            this.passwordHash = PasswordHasher.createHash(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Validates a password
     *
     * @param password the password to be verified
     * @return true if the password matches the stored password
     */
    public boolean validatePassword(String password) {
        boolean returnBool = false;
        try {
            if (!passwordHash.isEmpty()) {
                returnBool = PasswordHasher.validatePassword(password, passwordHash);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnBool;
    }

    public Photographer getPhotographer() {
        return photographer;
    }

    public void setPhotographer(Photographer photographer) {
        this.photographer = photographer;
    }

    public List<PictureSession> getSessions() {
        return sessions.stream().sorted().collect(toList());
    }
    
    /**
     * Finds a picture to display as representative of this shop.
     * This is a non-hidden picture that belongs to a public session.
     * @return p such that p in sessions and p not hidden and p.session public.
     */
    public Picture showcasePicture() {
        return sessions.stream().sorted().
                filter(s -> s.isPublic() && 
                s.getPictures().stream().
                        anyMatch(p -> !p.isHidden())).
                findFirst().
                flatMap(s -> s.getPictures().stream().sorted().findFirst()).
                orElse(null);
    }

    public static Shop getShopByID(int id) {
        Shop returnShop = null;
        try {
            Session session = HibernateSession.getInstance().getSession();
            returnShop = (Shop) session.load(Shop.class, id);
        } catch (HibernateException ex) {
            Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnShop;
    }

    public static Shop getShopByLogin(String login) {

        return HibernateEntityHelper.all(Shop.class).
                stream().
                filter(s -> s.getLogin().equals(login)).
                findAny().
                orElse(null);

    }
    
    /**
     * Checks ownership of a given picture session.
     * @param session The given session.
     * @return true if this shop owns the session
     */
    public boolean doesShopOwnPictureSession(PictureSession session){
        return (this.getId() == session.getShop().getId());
    }
    
    /**
     * Checks ownership of this shop to a user with a given user name.
     * @param username The given user name.
     * @return true if logged in user owns this shop.
     */
    public boolean doesUserOwnShop(String username){
        return this.login.equals(username);
    }
    
}
