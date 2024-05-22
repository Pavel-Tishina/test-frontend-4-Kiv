package com.itgnostic.test4sandbox.db.dao.impl;


import com.itgnostic.test4sandbox.db.dao.EmployeeDbService;
import com.itgnostic.test4sandbox.db.entity.EmployeeEntity;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.LongStream;


public class EmployeeDbServiceImpl implements EmployeeDbService {

    @PersistenceContext
    private final Session session;
    private final String entityName = EmployeeEntity.class.getName();



    public EmployeeDbServiceImpl(Session session) {
        this.session = session;
    }

    @Override
    @Transactional
    public Long add(EmployeeEntity e) {
        if (!session.isOpen() || e.getId() != null)
            return null;

        Transaction transaction = session.beginTransaction();

        Long out = (Long) session.save(e);
        //session.persist(e);
        //session.flush();
        transaction.commit();

        return out;
    }

    @Override
    @Transactional
    public EmployeeEntity get(long id) {
        if (!session.isOpen())
            return null;

        return session.get(EmployeeEntity.class, id);
    }

    @Override
    @Transactional
    public List<EmployeeEntity> get(long page, long limit) {
        String jpql = "FROM " + entityName;

        Query query = session.createQuery(jpql);
        query.setFirstResult((int) (page * limit));
        query.setMaxResults((int) limit);

        return query.list();
    }

    @Override
    public List<EmployeeEntity> getPossibleSupervisors(Long subId) {
        return executeQuery("SELECT e FROM %s e".formatted(entityName));
    }

    @Override
    @Transactional
    public List<EmployeeEntity> getList(long[] ids) {
        return getList(LongStream.of(ids).boxed().collect(Collectors.toSet()));
    }

    @Override
    @Transactional
    public List<EmployeeEntity> getList(Collection<Long> ids) {
        if (!session.isOpen())
            return null;

        return executeQuery("SELECT e FROM %s e WHERE e.id IN :ids".formatted(entityName), "ids", ids);
    }

    @Override
    @Transactional
    public EmployeeEntity modify(EmployeeEntity e) {
        if (!session.isOpen() || e == null || e.getId() == null)
            return null;

        EmployeeEntity existed = get(e.getId());
        if (existed == null || existed.equals(e) || !existed.getCreated().equals(e.getCreated()))
            return null;

        if (!Objects.equals(existed.getSupervisor(), e.getSupervisor()))
            existed.setSupervisor(e.getSupervisor());

        if (!Objects.equals(existed.getPosition(), e.getPosition()))
            existed.setPosition(e.getPosition());

        if (!Objects.equals(existed.getFirstName(), e.getFirstName()))
            existed.setFirstName(e.getFirstName());

        if (!Objects.equals(existed.getLastName(), e.getLastName()))
            existed.setLastName(e.getLastName());

        if (!Objects.equals(existed.getSubordinates(), e.getSubordinates()))
            existed.setSubordinates(e.getSubordinates() == null
                    ? new HashSet<>() : e.getSubordinates());

        Transaction transaction = session.beginTransaction();

        session.merge(existed);
        session.flush();
        transaction.commit();
        return existed;
    }

    @Override
    @Transactional
    public Boolean del(EmployeeEntity e) {
        if (!session.isOpen())
            return false;

        if (e.getId() == null)
            return null;

        Transaction transaction = session.beginTransaction();
        session.remove(e);
        session.flush();
        transaction.commit();

        return true;
    }

    @Override
    @Transactional
    public Boolean del(int id) {
        return del(get(id));
    }

    @Override
    @Transactional
    public Long getLastIndex() {
        if (!session.isOpen())
            return null;

        String jpql = "SELECT MAX(e.id) FROM " + entityName + " e";
        Query query = session.createQuery(jpql, EmployeeEntity.class);

        Object out = query.getSingleResult();

        return out == null ? null : (Long) out;
    }

    @Override
    @Transactional
    public Long getTotal() {
        Query<Long> query = session.createQuery("select count(*) from " + entityName, Long.class);
        return query.getSingleResult();
    }

    @Transactional
    protected List<EmployeeEntity> executeQuery(String sql, String param, Object val) {
        if (!session.isOpen())
            return null;

        Query<EmployeeEntity> query = session.createQuery(sql, EmployeeEntity.class);
        query.setParameter(param, val);

        return query.getResultList();
    }

    @Transactional
    protected List<EmployeeEntity> executeQuery(String sql) {
        if (!session.isOpen())
            return null;

        Query<EmployeeEntity> query = session.createQuery(sql, EmployeeEntity.class);

        return query.getResultList();
    }
}
