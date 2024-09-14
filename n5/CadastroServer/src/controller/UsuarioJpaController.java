package controller;

import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import model.Usuario;

public class UsuarioJpaController implements Serializable {

    private EntityManagerFactory emf = null;

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Adicionar o método findUsuario
    public Usuario findUsuario(String login, String senha) {
        EntityManager em = getEntityManager();
        try {
            // Criar a consulta JPA para buscar o usuário com login e senha
            Query query = em.createQuery("SELECT u FROM Usuario u WHERE u.nome = :login AND u.senha = :senha");
            query.setParameter("login", login);
            query.setParameter("senha", senha);
            try {
                // Tenta retornar o usuário encontrado
                return (Usuario) query.getSingleResult();
            } catch (NoResultException e) {
                // Retorna null se não houver resultado
                return null;
            }
        } finally {
            em.close();
        }
    }
}
