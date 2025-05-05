package brrrr.go.horsey.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class GameService {
    @Inject
    EntityManager em;

}
