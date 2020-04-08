package lufa.alfaserwis.CarManagment.controller;

import lufa.alfaserwis.CarManagment.entity.Car;
import lufa.alfaserwis.CarManagment.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class testController {

    CarService carService;

    @Autowired
    public testController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/hello")
    public String hello(Model model){
        List<Car> cars = carService.getAll();
        model.addAttribute("cars",cars);
        return "hello";
    }


}
