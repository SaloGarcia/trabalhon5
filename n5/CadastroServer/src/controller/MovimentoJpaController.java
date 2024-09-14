package controller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaQuery;
import model.Movimento;

public class MovimentoJpaController implements Serializable {

    private EntityManagerFactory emf = null;

    public MovimentoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Método para retornar todos os movimentos
    public List<Movimento> findMovimentoEntities() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Movimento> cq = em.getCriteriaBuilder().createQuery(Movimento.class);
            cq.select(cq.from(Movimento.class));
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    // Método para criar um novo movimento
    public void create(Movimento movimento) throws Exception {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(movimento);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new Exception("An error occurred while creating the movement", ex);
        } finally {
            em.close();
        }
    }
}
