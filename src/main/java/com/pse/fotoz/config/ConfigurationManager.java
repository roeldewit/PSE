package com.pse.fotoz.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

/**
 * Refactored base class to support multiple config files, leaving default
 * config intact
 *
 * @author René
 */
public class ConfigurationManager {

    protected static final int reloadDelayMS = 30000; //herlaadtijd voor xml, zodat de xml niet telkens gelezen hoeft te worden
    public static XMLConfiguration config;
    public static XMLConfiguration configPasswords;

    static {
        initConfig();
    }

    public static void initConfig() {
        try {
            config = new XMLConfiguration("application.cfg.xml");
            configPasswords = new XMLConfiguration("passwords.cfg.xml");
        } catch (ConfigurationException ex) {
            Logger.getLogger(ConfigurationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
        strategy.setRefreshDelay(reloadDelayMS);
        config.setReloadingStrategy(strategy);
        config.setValidating(true); //valideer de xml
    }

}
