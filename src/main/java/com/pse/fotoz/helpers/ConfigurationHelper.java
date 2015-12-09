/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.helpers;

import com.pse.fotoz.config.ConfigurationManager;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author René
 */
public class ConfigurationHelper {

    //---------general------------- 
    public static String getUrlprefix() {
        return ConfigurationManager.config.getString("general.urlprefix");
    }

    //-----fileupload---------
    public static int getMaxfilesizeinkb() {
        return ConfigurationManager.config.getInt("fileupload.maxfilesizeinkb");
    }

    public static int getMaxmultipartsizeinkb() {
        return ConfigurationManager.config.
                getInt("fileupload.maxmultipartsizeinkb");
    }

    public static List<String> getExtensionwhitelist() {
        return Arrays.asList(ConfigurationManager.config.
                getStringArray("fileupload.extensionwhitelist"));
    }

    //------filelocations-------
    public static String getGeneralAssetLocation() {
        return ConfigurationManager.config.
                getString("filelocations.generalassetlocation");
    }

    public static String getShopAssetLocation() {
        return ConfigurationManager.config.
                getString("filelocations.savelocationshops");
    }

    public static String getProductTypeAssetLocation() {
        return ConfigurationManager.config.
                getString("filelocations.savelocationproducttype");
    }

    //-----mollie---------
    public static String getMollieTestApiKey() {
        return ConfigurationManager.configPasswords.
                getString("mollie.testapikey");
    }

    public static String getMolliePaymentGetUrl() {
        return ConfigurationManager.config.
                getString("mollie.paymentgeturl");
    }

    public static String getMolliePaymentPostUrl() {
        return ConfigurationManager.config.
                getString("mollie.paymentposturl");
    }

    public static String getMollieRedirectUrl() {
        return ConfigurationManager.config.
                getString("mollie.redirecturl");
    }
}
