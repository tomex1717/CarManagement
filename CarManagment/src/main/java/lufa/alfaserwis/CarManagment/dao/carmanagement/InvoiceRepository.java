package lufa.alfaserwis.CarManagment.dao.carmanagement;

import lufa.alfaserwis.CarManagment.entity.carmanagement.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
}
