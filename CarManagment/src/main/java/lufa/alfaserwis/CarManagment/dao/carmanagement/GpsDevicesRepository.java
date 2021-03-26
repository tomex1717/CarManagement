package lufa.alfaserwis.CarManagment.dao.carmanagement;

import lufa.alfaserwis.CarManagment.entity.carmanagement.CarAssignmentToGpsDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GpsDevicesRepository extends JpaRepository<CarAssignmentToGpsDevice, String> {

}
