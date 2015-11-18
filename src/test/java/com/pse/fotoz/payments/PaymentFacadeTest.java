/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.payments;

import com.pse.fotoz.payments.domain.PaymentRequest;
import com.pse.fotoz.payments.domain.PaymentResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author René
 */
public class PaymentFacadeTest {
    
    public PaymentFacadeTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testCreatePayment() {
        
        PaymentRequest payC = new PaymentRequest();
        payC.setAmount(10.12d);
        payC.setDescription("description test");
        payC.setRedirectUrl("http://www.test.abc/redirecturlvanons");
        
        PaymentResponse result = PaymentFacade.CreatePayment(payC);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
