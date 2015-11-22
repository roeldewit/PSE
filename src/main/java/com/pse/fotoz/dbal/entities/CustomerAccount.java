/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.dbal.entities;

import com.pse.fotoz.helpers.encryption.PasswordHash;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author 310054544
 */
@Entity
@Table(name = "customer_accounts")
public class CustomerAccount implements HibernateEntity {
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Customer customer;

    @Basic
    @Column(name = "login", unique = true)
    @Size(min=1, max=100, message = "{error_size_login}")
    private String login;

    @Basic
    @Column(name = "passwordHash")
    @Size(min=4, message = "{error_size_password}")
    private String passwordHash;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "customer_permissions", joinColumns = { 
                        @JoinColumn(name = "customer_account_id", 
                                nullable = false, updatable = false)
                    }, inverseJoinColumns = { 
                        @JoinColumn(name = "picture_session_id", 
                                nullable = false, updatable = false) 
                    })
    private Set<PictureSession> permittedSessions;

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPasswordHash(String passwordHash) {
        try {
            this.passwordHash = PasswordHash.createHash(passwordHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Set<PictureSession> getPermittedSessions() {
        return permittedSessions;
    }

    public void setPermittedSessions(Set<PictureSession> permittedSessions) {
        this.permittedSessions = permittedSessions;
    }
    
    /**
     * Validates a password 
     * @param password the password to be verified
     * @return true if the password matches the stored password
     */
    public boolean validatePassword(String password) {
        boolean returnBool = false;
        try {
            if(!passwordHash.isEmpty()){
               returnBool = PasswordHash.validatePassword(password, passwordHash);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnBool;
    }
    
}
