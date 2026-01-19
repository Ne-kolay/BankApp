package savchenko.dev;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class TransactionHelper {

    private final SessionFactory sessionFactory;

    public TransactionHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Для void-операций
    public void executeInTransaction(Consumer<Session> action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();

        //если транзакция уже есть, просто юзаем ее и выходим
        if (!transaction.getStatus().equals(TransactionStatus.NOT_ACTIVE)) {
            action.accept(session);
            return;
        }

        //если нет - открываем транзакцию, юзаем её и закрывааем
        try {
            transaction.begin();
            action.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    // Для действий с возвращаемым результатом
    public <T> T executeInTransaction(Function<Session, T> action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();

        if (!transaction.getStatus().equals(TransactionStatus.NOT_ACTIVE)) {
            return action.apply(session);
        }

        try {
            transaction.begin();
            T result = action.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }
}