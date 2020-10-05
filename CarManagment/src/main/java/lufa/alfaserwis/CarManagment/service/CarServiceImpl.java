package lufa.alfaserwis.CarManagment.service;

import lufa.alfaserwis.CarManagment.config.YAMLConfig;
import lufa.alfaserwis.CarManagment.dao.carmanagement.CarRepository;
import lufa.alfaserwis.CarManagment.entity.carmanagement.Car;
import lufa.alfaserwis.CarManagment.exceptionHandling.CarNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
@Service
public class CarServiceImpl implements CarService {

    private CarRepository carRepository;
    private YAMLConfig config;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, YAMLConfig config) {
        this.carRepository = carRepository;
        this.config = config;
    }

    @Override
    public List<Car> getAll() {
        List<Car> carList = carRepository.findByOrderByRegNumberAsc();


        return carList;
    }

    @Override
    public Car getByRegNumber(String regNumber) {

        Optional<Car> result = carRepository.findById(regNumber);
        Car car;
        if (result.isPresent()) {
            car = result.get();
        }
        else {

            throw new CarNotFoundException("Nie znaleziono samochodu o podanym numerze rejestracyjnym: " + regNumber);
        }


        return car;
    }



    @Override
    public void save(Car car) {

//        save pic first if selected
        if(!car.getCarPic().isEmpty()) {
            try {

                // Get the file and save it somewhere
                byte[] bytes = car.getCarPic().getBytes();
                Path path = Paths.get(config.getPhotosPath() + car.getCarPic().getOriginalFilename());
                car.setCarPicName(car.getCarPic().getOriginalFilename());
                car.setCarPicPath(path.toString());
                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        save data to db
        carRepository.save(car);
    }

    @Override
    public void deleteById(String regNumber) {
        carRepository.deleteById(regNumber);
    }
}
