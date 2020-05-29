package lufa.alfaserwis.CarManagment.service;

import lufa.alfaserwis.CarManagment.dao.user.UserRepository;
import lufa.alfaserwis.CarManagment.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User findByUserName(String username) {
       return userRepository.getOne(username);
    }
}
