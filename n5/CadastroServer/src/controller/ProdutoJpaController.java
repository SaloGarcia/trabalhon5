package controller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
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

    // Método para encontrar um produto por ID
    public Produto findProduto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produto.class, id);
        } finally {
            em.close();
        }
    }

    // Método para editar um produto
    public void edit(Produto produto) throws Exception {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(produto);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new Exception("An error occurred while editing the product", ex);
        } finally {
            em.close();
        }
    }
}
