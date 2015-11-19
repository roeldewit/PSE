package com.pse.fotoz.dbal.entities;

import com.pse.fotoz.dbal.HibernateException;
import com.pse.fotoz.dbal.HibernateSession;
import java.io.Serializable;
import org.hibernate.Session;

/**
 * Basic annotation interface for denoting entities that are persisted through
 * Hibernate.
 * @author Robert
 */
public interface HibernateEntity extends Serializable, 
        Comparable<HibernateEntity> {
    
    abstract public int getId();
    
    /**
     * Persists this entity.
     * Either inserts a new entity or updates an existing entity.
     * @throws HibernateException If an error occured attempting to persist the
     * entity
     */
    public default void persist() throws HibernateException {
        final Session session = HibernateSession.getInstance().newSession();
        
        session.beginTransaction();
        session.saveOrUpdate(this);
        session.getTransaction().commit();
        session.close();
    }
    
    /**
     * Deletes this entity.
     * Removed this entity from the database.
     * @throws HibernateException If an error occured attempting to delete the
     * entity
     */
    public default void delete() throws HibernateException {
        final Session session = HibernateSession.getInstance().newSession();
        
        session.beginTransaction();
        session.delete(this);
        session.getTransaction().commit();
        session.close();
    }
    
    /**
     * Default ordering on entities is done through comparing the identities.
     * That is, for entities e1, e2, e1 &lt; e2 iff e1.getId() &lt; e2.getId().
     * This means to provide determinism for iterating over collections.
     * Entities that have a more semantic ordering should override this method.
     * @see java.lang.Comparable
     */    
    @Override
    public default int compareTo(HibernateEntity e) {
        return e.getId() - getId();
    }
}
