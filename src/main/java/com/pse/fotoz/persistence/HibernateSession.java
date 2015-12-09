package com.pse.fotoz.persistence;

import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;


/**
 * Class setting up and providing a session with Hibernate.
 * @author Robert
 */
public class HibernateSession {    
    private static Optional<HibernateSession> instance = Optional.empty();
    
    private final StandardServiceRegistry registry;
    private final SessionFactory sessionFactory;
    
    /**
     * Configures a basic Hibernate SessionFactory.
     * @throws HibernateException 
     */
    private HibernateSession() throws HibernateException {
        this.registry = new StandardServiceRegistryBuilder().
			configure().
			build();
        
        try {
            this.sessionFactory = new MetadataSources(registry).
                buildMetadata().
                buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(registry);
            
            throw e;
        }
    }
    
    /**
     * Returns the singleton instance of this class.
     * @return Singleton instance of this class
     * @throws HibernateException if an error occured setting up the Hibernate
     * session
     */
    public static HibernateSession getInstance() throws HibernateException {
        if (!instance.isPresent()) {
            instance = Optional.of(new HibernateSession());
        }
        
        return instance.get();
    }
    
    /**
     * Gives the default session
     * @return Default session.
     */
    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
