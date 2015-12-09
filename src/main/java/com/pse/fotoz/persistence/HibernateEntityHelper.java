package com.pse.fotoz.persistence;

import com.pse.fotoz.domain.entities.HibernateEntity;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import static org.hibernate.criterion.CriteriaSpecification.DISTINCT_ROOT_ENTITY;
import org.hibernate.criterion.Restrictions;

/**
 * Helper class for Hibernate entities. Provides basic persistance and retrieval
 * abstractions of Hibernate entities.
 *
 * @author Robert
 */
public class HibernateEntityHelper {

    /**
     * Returns all persisted entities from the database.
     *
     * @param <T> The type of the entities
     * @param c The class type of the entities
     * @return A list of all entities of type T
     */
    public static <T extends HibernateEntity> List<T> all(Class<T> c) {
        try {
            Session session = HibernateSession.getInstance().getSession();
            session.beginTransaction();
            List<T> result = session.createCriteria(c).
                    setResultTransformer(DISTINCT_ROOT_ENTITY).list();
            session.getTransaction().commit();
            return result;
        } catch (HibernateException ex) {
            Logger.getLogger(HibernateEntityHelper.class.getName()).
                    log(Level.SEVERE, null, ex);
            return Collections.EMPTY_LIST;
        }

    }

    /**
     * Returns an entity from the database by its identity.
     *
     * @param <T> The type of the entity
     * @param c The class type of the entity
     * @param id The unique identifier of the entity
     * @return The entity of type T with given identity, or empty if none exist.
     */
    public static <T extends HibernateEntity> Optional<T> byId(Class<T> c,
            int id) {
        try {
            Session session = HibernateSession.getInstance().getSession();
            session.beginTransaction();
            Optional<T> result = Optional.ofNullable(session.get(c, id));
            session.getTransaction().commit();
            return result;
        } catch (HibernateException ex) {
            Logger.getLogger(HibernateEntityHelper.class.getName()).
                    log(Level.SEVERE, null, ex);
            return Optional.empty();
        }
    }

    /**
     * Finds all entities matching a field-value pair.
     *
     * @param <T> The type of the entity
     * @param c The class type of the entity
     * @param fieldName The name of the field to be matched
     * @param fieldValue The value of the field to be matched
     * @return A list of all entities matching the given field-value pair
     */
    public static <T extends HibernateEntity> List<T> find(Class<T> c,
            String fieldName, Object fieldValue) {
        try {

            Session session = HibernateSession.getInstance().getSession();

            session.beginTransaction();
            Criteria criteria = HibernateSession.getInstance().getSession().
                    createCriteria(c);
            List<T> result = criteria.add(Restrictions.eq(fieldName, fieldValue)).list();
            session.getTransaction().commit();
            return result;
        } catch (HibernateException ex) {
            Logger.getLogger(HibernateEntityHelper.class.getName()).
                    log(Level.SEVERE, null, ex);
            return Collections.EMPTY_LIST;
        }
    }
}
