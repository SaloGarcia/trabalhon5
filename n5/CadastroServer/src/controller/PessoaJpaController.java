package controller;

import java.io.Serializable;
import java.util.List;  // Importar a classe List
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaQuery;
import model.Pessoa;

public class PessoaJpaController implements Serializable {

    private EntityManagerFactory emf = null;

    public PessoaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Método para retornar todas as pessoas
    public List<Pessoa> findPessoaEntities() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Pessoa> cq = em.getCriteriaBuilder().createQuery(Pessoa.class);
            cq.select(cq.from(Pessoa.class));
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

}
