package com.JavaProje.KurumArizaTakipSistemi.dao;



import com.JavaProje.KurumArizaTakipSistemi.model.Role;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
}