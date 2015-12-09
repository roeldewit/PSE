/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.payments;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

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

    //debug test, kan later verwijderd worden
    
//    @Test
//    public void testCreatePayment() {
//        String s2 = ConfigurationHelper.getMolliePaymentGetUrl();
//        String s = ConfigurationHelper.getGeneralAssetLocation();
//        
//        PaymentRequest payC = new PaymentRequest();
//        payC.setAmount(10.12d);
//        payC.setDescription("description test");
//        payC.setRedirectUrl("http://www.test.abc/redirecturlvanons");
//        //payC.setWebhookUrl("http://www.test.abc/");
//        payC.setLocale(Locale.GERMANY);
//        
//        PaymentFacade pmf  = new PaymentFacade();
//       // pmf.setIsDebug(true);
//        //pmf.setUseProxy(true);
//        PaymentResponse result = pmf.CreatePayment(payC).get();
//        PaymentResponse result2 = pmf.GetPayment(result.getId()).get();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
}
