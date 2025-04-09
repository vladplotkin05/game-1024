package sk.tuke.gamestudio.service;


import sk.tuke.gamestudio.entity.Rating;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class RatingServiceJPA implements RatingService{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        Rating existingRating = null;
        try {
            existingRating = entityManager.createQuery(
                            "SELECT r FROM Rating r WHERE r.player = :player AND r.game = :game", Rating.class)
                    .setParameter("player", rating.getPlayer())
                    .setParameter("game", rating.getGame())
                    .getSingleResult();
        } catch (Exception e) {
            // No existing rating, we'll just save a new
        }

        if (existingRating != null) {
            existingRating.setRating(rating.getRating());
            existingRating.setRatedOn(rating.getRatedOn());
            entityManager.merge(existingRating);
        } else {
            entityManager.persist(rating);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        Double average = entityManager.createQuery(
                        "SELECT AVG(r.rating) FROM Rating r WHERE r.game = :game", Double.class)
                .setParameter("game", game)
                .getSingleResult();
        return average == null ? 0 : average.intValue();
    }

    @Override
    public int getRating(String player, String game) throws RatingException {
        try {
            return entityManager.createQuery(
                    "SELECT r.rating FROM Rating r WHERE r.player = :player AND r.game = :game", Integer.class)
                    .setParameter("player", player)
                    .setParameter("game", game)
                    .getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void reset() throws RatingException {
        entityManager.createNativeQuery("DELETE FROM rating").executeUpdate();
    }
}
