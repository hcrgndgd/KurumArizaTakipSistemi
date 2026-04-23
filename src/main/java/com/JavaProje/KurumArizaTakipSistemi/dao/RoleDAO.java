package com.JavaProje.KurumArizaTakipSistemi.dao;



import com.JavaProje.KurumArizaTakipSistemi.model.Role;
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
        return getSession()
                .createQuery("FROM Role r WHERE r.roleName = :name", Role.class)
                .setParameter("name", roleName)
                .getResultStream()
                .findFirst();
    }

    public Optional<Role> findById(long roleId) {
        return Optional.ofNullable(getSession().get(Role.class, (int) roleId));
    }

    public void save(Role role) {
        getSession().persist(role);
    }

    public void delete(Role role) {
        getSession().remove(role);
    }

    public List<Role> findAll() {
        return getSession()
                .createQuery("FROM Role r", Role.class)
                .getResultList();
    }
}
