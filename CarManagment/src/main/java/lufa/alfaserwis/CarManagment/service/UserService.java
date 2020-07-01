package lufa.alfaserwis.CarManagment.service;

import lufa.alfaserwis.CarManagment.entity.user.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    void saveUser(User user);

    User findByUserName(String username);

    void deleteUser(String username);

    void deleteUser(User user);

}
