package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Comment;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class CommentServiceJPA implements CommentService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addComment(Comment comment) throws CommentException {
        entityManager.persist(comment);
    }

    @Override
    public List<Comment> getComments(String game) throws CommentException {
        return entityManager.createQuery(
                        "SELECT c FROM Comment c WHERE c.game = :game ORDER BY c.commentedOn DESC", Comment.class)
                .setParameter("game", game)
                .getResultList();
    }

    @Override
    public void reset() throws CommentException {
        entityManager.createNativeQuery("DELETE FROM comment").executeUpdate();
    }
}
