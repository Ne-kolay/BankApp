package savchenko.dev.Repositories;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import savchenko.dev.Model.Account;
import java.util.Optional;
import java.util.List;

@Repository
public class AccountRepository {

    //CREATE
    public Account save(Account account, Session session) {
        session.persist(account);
        return account;
    }

    //READ
    public Optional<Account> findById(Long id, Session session) {
        String jpql = "from Account a where a.id = :id";
        return session.createQuery(jpql, Account.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    public List<Account> findAllByUserId(Long userId, Session session) {
        String jpql = "from Account a where a.user.id = :userId order by a.id";
        return session.createQuery(jpql, Account.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public boolean existsById(Long id, Session session) {
        String jpql = "select count(a) from Account a where a.id = :id";
        Long count = session.createQuery(jpql, Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }

    //UPDATE
    public Account update(Account account, Session session) {
        return session.merge(account);
    }

    //DELETE
    public void delete(Account account, Session session) {
        session.remove(account);
    }

    public void deleteById(Long id, Session session) {
        findById(id, session).ifPresent(session::remove);
    }

}