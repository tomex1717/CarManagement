package lufa.alfaserwis.CarManagment.service;

import lufa.alfaserwis.CarManagment.dao.CarRepository;
import lufa.alfaserwis.CarManagment.entity.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class CarServiceImpl implements CarService {

    CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<Car> getAll() {

        return carRepository.findByOrderByRegNumberAsc();
    }

    @Override
    public Car getByRegNumber(String regNumber) {

        Optional<Car> result = carRepository.findById(regNumber);
        Car car;
        if (result.isPresent()) {
            car = result.get();
        }
        else {

            throw new RuntimeException("Nie znaleziono samochodu o podanym numerze rejestracyjnym" + regNumber);
        }
        return car;
    }

    @Override
    public void save(Car car) {
        carRepository.save(car);
    }

    @Override
    public void deleteById(String regNumber) {
        carRepository.deleteById(regNumber);
    }
}
