package controller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaQuery;
import model.Produto;

public class ProdutoJpaController implements Serializable {

    private EntityManagerFactory emf = null;

    public ProdutoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Método para retornar todos os produtos
    public List<Produto> findProdutoEntities() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Produto> cq = em.getCriteriaBuilder().createQuery(Produto.class);
            cq.select(cq.from(Produto.class));
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }
}
