package brrrr.go.horsey.service;

import brrrr.go.horsey.orm.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserService {

    @Inject
    EntityManager em;

    @Transactional
    public void createUser(User user) {
        em.persist(user);
    }

}
