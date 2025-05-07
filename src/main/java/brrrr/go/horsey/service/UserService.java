package brrrr.go.horsey.service;

import brrrr.go.horsey.orm.User;
import io.quarkus.narayana.jta.runtime.TransactionConfiguration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    EntityManager em;

    @Transactional
    public User createUser(User user) {
        em.persist(user);
        return user;
    }

    public User getUser(String username) throws NotFoundException {
        try {
            return em.find(User.class, username);
        } catch (NoResultException e) {
            throw new NotFoundException();
        }
    }

}
