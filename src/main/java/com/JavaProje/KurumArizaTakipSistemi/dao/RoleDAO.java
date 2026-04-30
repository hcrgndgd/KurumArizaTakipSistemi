package com.JavaProje.KurumArizaTakipSistemi.dao;

import com.JavaProje.KurumArizaTakipSistemi.model.Role;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Optional<Role> findByName(String roleName) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Role> cq = cb.createQuery(Role.class);

        Root<Role> root = cq.from(Role.class);

        Predicate condition = cb.equal(root.get("roleName"), roleName);

        cq.select(root).where(condition);

        return getSession()
                .createQuery(cq)
                .getResultStream()
                .findFirst();
    }

    public Optional<Role> findById(long roleId) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Role> cq = cb.createQuery(Role.class);

        Root<Role> root = cq.from(Role.class);

        Predicate condition = cb.equal(root.get("roleId"), roleId);

        cq.select(root).where(condition);

        return getSession()
                .createQuery(cq)
                .getResultStream()
                .findFirst();
    }

    public void save(Role role) {
        getSession().persist(role);
    }

    public void update(Role role) {
        getSession().merge(role);
    }

    public void delete(Role role) {
        getSession().remove(role);
    }

    public List<Role> findAll() {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Role> cq = cb.createQuery(Role.class);

        Root<Role> root = cq.from(Role.class);

        cq.select(root);

        return getSession()
                .createQuery(cq)
                .getResultList();
    }
}
