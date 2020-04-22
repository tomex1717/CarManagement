package lufa.alfaserwis.CarManagment.controller;

import lufa.alfaserwis.CarManagment.entity.Car;
import lufa.alfaserwis.CarManagment.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class CarManagementController {

    CarService carService;

    @Autowired
    public CarManagementController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/carmanagement")
    public String hello(Model model){
        List<Car> cars = carService.getAll();
        model.addAttribute("cars",cars);
        return "index";
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
    public String saveCar(@ModelAttribute(name = "car") Car car){
        if(car.getGasOverviewDate() == null || car.getGasOverviewDate().isEmpty()){
            car.setGasOverviewDate("1900-01-01");
        }
        if(car.getOilDate() == null || car.getOilDate().isEmpty() ){
            car.setOilDate("1900-01-01");
        }
        if(car.getOverviewDate() == null || car.getOverviewDate().isEmpty()){
            car.setOverviewDate("1900-01-01");
        }
        if(car.getFuelCardExpire() == null || car.getFuelCardExpire().isEmpty()){
            car.setFuelCardExpire("1900-01-01");
        }
        if(car.getFirstRegDate() == null || car.getFirstRegDate().isEmpty()){
            car.setFirstRegDate("1900-01-01");
        }
        if(car.getOcDate() == null || car.getOcDate().isEmpty()){
            car.setOcDate("1900-01-01");
        }
        carService.save(car);
        return "redirect:/";
    }


    @GetMapping("/deletecar")
    public String deleteCar(@RequestParam("regNumber") String regNumber){
        carService.deleteById(regNumber);
        return "redirect:/";

    }

}
