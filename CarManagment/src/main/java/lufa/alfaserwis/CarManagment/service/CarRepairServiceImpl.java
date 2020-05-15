package lufa.alfaserwis.CarManagment.service;

import lombok.extern.slf4j.Slf4j;
import lufa.alfaserwis.CarManagment.dao.CarRepairRepository;
import lufa.alfaserwis.CarManagment.dao.InvoiceRepository;
import lufa.alfaserwis.CarManagment.entity.CarRepair;
import lufa.alfaserwis.CarManagment.entity.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
public class CarRepairServiceImpl implements CarRepairService {

//    fields
    private CarRepairRepository carRepairRepository;
    private InvoiceRepository invoiceRepository;


//    constructors

    @Autowired
    public CarRepairServiceImpl(CarRepairRepository carRepairRepository, InvoiceRepository invoiceRepository) {
        this.carRepairRepository = carRepairRepository;
        this.invoiceRepository = invoiceRepository;
    }




//    public methods

    @Override
    public List<CarRepair> getAll() {
        return carRepairRepository.findAll();
    }

    @Override
    public List<CarRepair> getByRegNumber(String regNumber) {
        return carRepairRepository.findByRegNumber(regNumber);
    }

    @Override
    public void save(CarRepair carRepair) {

//        save file to filesystem first
        if(!carRepair.getInvoice().isEmpty()) {
            try {

                // Get the file and save it somewhere
                byte[] bytes = carRepair.getInvoice().getBytes();
                Path path = Paths.get(lufa.alfaserwis.utils.Paths.INVOICES_PATH + carRepair.getInvoice().getOriginalFilename());
                Invoice invoice = new Invoice();
                invoice.setFileName(carRepair.getInvoice().getOriginalFilename());
                carRepair.addInvoice(invoice);

                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



//        save object
        carRepairRepository.save(carRepair);
    }

    @Override
    public void deleteById(int id) {
        carRepairRepository.deleteById(id);
    }

    @Override
    public CarRepair getById(int id) {
        Optional<CarRepair> result = carRepairRepository.findById(id);
        CarRepair carRepair;
        if (result.isPresent()) {
            carRepair = result.get();
        }
        else {

            throw new RuntimeException("Nie znaleziono naprawy samochodu o podanym ID: " + id);
        }

        return carRepair;
    }

    public void deleteInvoice(Invoice invoice){
        try{
            Files.delete(Paths.get(lufa.alfaserwis.utils.Paths.INVOICES_PATH+invoice.getFileName()));

        } catch (IOException e){
            log.error("nie można usunąć faktury");
        }

        invoiceRepository.delete(invoice);
        log.info("Deleted invoice: id=" + invoice.getId());
    }



//    private methods









    //TODO deleting invoices from db and filesystem


}
