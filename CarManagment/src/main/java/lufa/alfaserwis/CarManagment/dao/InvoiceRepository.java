package lufa.alfaserwis.CarManagment.dao;

import lufa.alfaserwis.CarManagment.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
}
