package lufa.alfaserwis.CarManagment.service;

import lufa.alfaserwis.CarManagment.entity.CarRepair;
import lufa.alfaserwis.CarManagment.entity.Invoice;

import java.util.List;

public interface CarRepairService {

    List<CarRepair> getAll();
    List<CarRepair> getByRegNumber(String regNumber);
    void save(CarRepair carRepair);
    void deleteById(int id);
    CarRepair getById(int id);
    void deleteInvoice(int id);
    Invoice getInvoiceById(int id);


}
