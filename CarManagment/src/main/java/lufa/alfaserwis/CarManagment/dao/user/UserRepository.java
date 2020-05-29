package lufa.alfaserwis.CarManagment.dao.user;

import lufa.alfaserwis.CarManagment.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, String> {
}
