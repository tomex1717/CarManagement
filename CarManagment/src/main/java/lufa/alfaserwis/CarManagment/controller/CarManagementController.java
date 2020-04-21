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

        System.out.println("data:" + car.getOcDate());

        carService.save(car);
        return "redirect:/";
    }


}
