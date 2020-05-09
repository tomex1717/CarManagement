package lufa.alfaserwis.CarManagment.service;

import lufa.alfaserwis.CarManagment.dao.CarRepairRepository;
import lufa.alfaserwis.CarManagment.entity.CarRepair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarRepairServiceImpl implements CarRepairService {

    private CarRepairRepository carRepairRepository;

    @Autowired
    public CarRepairServiceImpl(CarRepairRepository carRepairRepository) {
        this.carRepairRepository = carRepairRepository;
    }

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
}
