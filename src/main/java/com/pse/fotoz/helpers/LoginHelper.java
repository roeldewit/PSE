/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.helpers;

import com.pse.fotoz.domain.entities.CustomerAccount;
import com.pse.fotoz.domain.entities.ProducerAccount;
import com.pse.fotoz.domain.entities.Shop;
import com.pse.fotoz.persistence.HibernateEntityHelper;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author René van de Vorst
 */
public class LoginHelper {

    /**
     * Gets current signed in username, regardless of type(needs checking)
     *
     * @return
     */
    public static Optional<String> currentUsername() {
        String result = SecurityContextHolder.getContext().
                getAuthentication().getName();

        return result == null ? Optional.empty() : Optional.of(result);
    }

    public static Optional<Shop> currentShopAccount() {
        if (!currentUsername().isPresent()) {
            return Optional.empty();
        }

        return HibernateEntityHelper.find(Shop.class, "login",
                currentUsername().get()).stream().
                findAny();
    }

    public static Optional<CustomerAccount> currentUserAccount() {
        if (!currentUsername().isPresent()) {
            return Optional.empty();
        }

        return HibernateEntityHelper.find(CustomerAccount.class, "login",
                currentUsername().get()).stream().
                findAny();
    }

    public static Optional<ProducerAccount> currentProducerAccount() {
        if (!currentUsername().isPresent()) {
            return Optional.empty();
        }

        return HibernateEntityHelper.find(ProducerAccount.class, "login",
                currentUsername().get()).stream().
                findAny();
    }
}
