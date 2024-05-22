package com.itgnostic.test4sandbox.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtils {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    //.configure("META-INF/persistence.xml")
                    .configure("hibernate.cfg.xml")
                    .build();

            return new MetadataSources(serviceRegistry)
                    .addAnnotatedClass(EmployeeUtils.class)
                    .addPackage("com.itgnostic.test4sandbox.db.entity")
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception ex) {
            System.err.println("Initial SessionFactory creation failed. " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
