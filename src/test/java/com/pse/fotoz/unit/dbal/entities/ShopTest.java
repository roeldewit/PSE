/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.unit.dbal.entities;

import com.pse.fotoz.domain.entities.Photographer;
import com.pse.fotoz.domain.entities.Shop;
import com.pse.fotoz.helpers.PersistenceFacade;
import com.pse.fotoz.persistence.HibernateException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author René van de Vorst
 */
public class ShopTest {

    private Photographer photographer;
    private Shop shop;

    public ShopTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws HibernateException {       
    }

    @After
    public void tearDown() throws HibernateException {
    }

    @Test
    public void testPasswordHashing() {
        photographer = new Photographer();
        photographer.setAddress("Molenaar 24");
        photographer.setCity("Eindhoven");
        photographer.setEmail("info@mooiekiekjes.nl");
        photographer.setName("Mooie Kiekjes Eindhoven");
        photographer.setPhone("040-9573238");
        shop = new Shop();
        shop.setPhotographer(photographer);
        shop.setLogin("winkel1");
        shop.setPassword("123");
        
        assertTrue(shop.validatePassword("123"));
        assertFalse(shop.validatePassword("random crap"));
    }
}
