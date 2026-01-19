package savchenko.dev.Services;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import savchenko.dev.TransactionHelper;
import savchenko.dev.Model.User;
import savchenko.dev.Repositories.UserRepository;

import java.util.List;


@Service
public class UserService {

    private final AccountService accountService;
    private final SessionFactory sessionFactory;
    private final TransactionHelper transactionHelper;
    private final UserRepository userRepository;

    public UserService(AccountService accountService,
                       SessionFactory sessionFactory,
                       TransactionHelper transactionHelper, UserRepository userRepository) {
        this.accountService = accountService;
        this.sessionFactory = sessionFactory;
        this.transactionHelper = transactionHelper;
        this.userRepository = userRepository;
    }


    //CREATE
    public User createUser(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be empty");
        }

        return transactionHelper.executeInTransaction(session -> {
            if (userRepository.existsByName(username, session)) {
                throw new IllegalArgumentException("User with name " + username + " already exists");
            }
            User user = new User(username);
            userRepository.saveUser(user, session);
            accountService.createFirstAccount(user.getId());
            return user;
        });
    }

    //READ
    public boolean existsByName(String name) {
        return transactionHelper.executeInTransaction(session -> {
            return userRepository.existsByName(name, session);
        });
    }

    public User getById(Long userId) {
        return transactionHelper.executeInTransaction(session -> {
             return userRepository.findById(userId, session)
                     .orElseThrow(() -> new IllegalArgumentException(
                             "User with id " + userId + " does not exist"
                     ));
        });
    }

    public User getByName(String name) {
        return transactionHelper.executeInTransaction(session -> {
            return userRepository.findByName(name, session)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "User with name " + name + " does not exist"
                    ));
        });
    }

    public List<User> getAllUsers() {
        return transactionHelper.executeInTransaction(session -> {
            List<User> users = userRepository.findAll(session);
            System.out.println("Found " + users.size() + " users");
            return users;
        });
    }
}


