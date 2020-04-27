package lufa.alfaserwis.CarManagment.controller;

import lufa.alfaserwis.CarManagment.entity.Car;
import lufa.alfaserwis.CarManagment.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/")
public class CarManagementController {

    CarService carService;

    @Autowired
    public CarManagementController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/")
    public String home(Model model){
        List<Car> cars = carService.getAll();



        model.addAttribute("cars",cars);
        return "home";
    }

    @GetMapping("/updatecar")
    public String carForm(@RequestParam("regNumber") String regNumber, Model model){
        Car car = carService.getByRegNumber(regNumber);
        model.addAttribute(car);

        return "car-form";
    }

    @PostMapping("/savecar")
    public String saveCar(@RequestParam("carPic") MultipartFile carPic, @ModelAttribute(name = "car") Car car){
        if(car.getGasOverviewDate().isEmpty()){
            car.setGasOverviewDate(null);
        }
        if(car.getOilDate().isEmpty() ){
            car.setOilDate(null);
        }
        if( car.getOverviewDate().isEmpty()){
            car.setOverviewDate(null);
        }
        if(car.getFuelCardExpire() == null || car.getFuelCardExpire().isEmpty()){
            car.setFuelCardExpire(null);
        }
        if(car.getFirstRegDate().isEmpty()){
            car.setFirstRegDate(null);
        }
        if(car.getOcDate().isEmpty()){
            car.setOcDate(null);
        }
        if(car.getGasCylinderExpire().isEmpty()){
            car.setGasCylinderExpire(null);
        }

        carService.save(car);
        return "redirect:/";
    }


    @GetMapping("/deletecar")
    public String deleteCar(@RequestParam("regNumber") String regNumber){
        carService.deleteById(regNumber);
        return "redirect:/";

    }

    @GetMapping("/addcar")
    public String addCar(Model model){
        Car car = new Car();
        model.addAttribute(car);
        return "car-form";

    }

}
