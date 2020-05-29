package lufa.alfaserwis.CarManagment.dao.user;

import lufa.alfaserwis.CarManagment.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
