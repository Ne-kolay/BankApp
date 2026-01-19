package savchenko.dev.Repositories;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import savchenko.dev.Model.User;
import java.util.Optional;
import java.util.List;


@Repository
public class UserRepository {

    //CREATE
    public User saveUser(User user, Session session) {
        session.persist(user);
        return user;
    }

    //READ
    public Optional<User> findById(Long id, Session session) {
        String jpql = "from User u where u.id = :id";
        return session.createQuery(jpql, User.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    public Optional<User> findByName(String name, Session session) {
        String jpql = "from User u where lower(u.name) = :name";
        return session.createQuery(jpql, User.class)
                .setParameter("name", name.toLowerCase())
                .uniqueResultOptional();
    }

    public List<User> findAll(Session session) {
        String jpql = "from User";
        return session.createQuery(jpql, User.class)
                .getResultList();
    }

    public boolean existsByName(String name, Session session) {
        String jpql = "select 1 from User u where u.name = :name";
        return session.createQuery(jpql)
                .setParameter("name", name)
                .setMaxResults(1)
                .uniqueResult() != null;
    }

    //UPDAE
    public void update(User user, Session session) {
        session.merge(user);
    }

    //DELETE
    public void deleteById(Long id, Session session) {
        findById(id, session).ifPresent(session::remove);
    }

}