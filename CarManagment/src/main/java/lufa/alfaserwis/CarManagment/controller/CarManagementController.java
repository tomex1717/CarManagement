package lufa.alfaserwis.CarManagment.controller;

import lufa.alfaserwis.CarManagment.entity.Car;
import lufa.alfaserwis.CarManagment.entity.CarRepair;
import lufa.alfaserwis.CarManagment.service.CarRepairService;
import lufa.alfaserwis.CarManagment.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/")
public class CarManagementController {

    private CarService carService;
    private CarRepairService carRepairService;
    @Autowired
    public CarManagementController(CarService carService,CarRepairService carRepairService) {
        this.carService = carService;
        this.carRepairService = carRepairService;
    }

    @GetMapping("/")
    public String home(Model model){
        List<Car> cars = carService.getAll();
        model.addAttribute("cars",cars);
        return "card-view";
    }

    @GetMapping("/tableview")
    public String tableView(Model model){
        List<Car> cars = carService.getAll();
        model.addAttribute("cars",cars);
        return "table-view";

    }

    @GetMapping("/updatecar")
    public String carForm(@RequestParam("regNumber") String regNumber, Model model){
        Car car = carService.getByRegNumber(regNumber);
        model.addAttribute(car);

        return "car-form";
    }

    @PostMapping("/savecar")
    public String saveCar(@RequestParam("carPic") MultipartFile carPic,@Valid @ModelAttribute(name = "car") Car car,
                         BindingResult bindingResult){
        if(car.getGasOverviewDate().isEmpty()){
            car.setGasOverviewDate(null);
        }
        if(car.getOilDate().isEmpty() ){
            car.setOilDate(null);
        }
        if(car.getOverviewDate().isEmpty()){
            car.setOverviewDate(null);
        }
        if(car.getFuelCardExpire().isEmpty()){
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
        if(car.getCarPicName()==null){
            car.setCarPicName("car-pic-not-found.webp");
        }
        if (bindingResult.hasErrors()) {
            return "car-form";
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
        car.setNew(true);
        car.setCarPicName("car-pic-not-found.webp");
        model.addAttribute(car);
        return "car-form";

    }

    @GetMapping("/showrepairs")
    public String showRepairs(Model model, @RequestParam("regNumber") String regNumber){
        model.addAttribute("regNumber" , regNumber);

        model.addAttribute("repairs",carRepairService.getByRegNumber(regNumber));
        return "repair-view";
    }

    @GetMapping("/addcarrepair")
    public String addCarRepair(Model model, @RequestParam("regNumber") String   regNumber){
        CarRepair carRepair = new CarRepair();
        carRepair.setRegNumber(regNumber);
        model.addAttribute("carRepair", carRepair);
        Car car = carService.getByRegNumber(regNumber);
        model.addAttribute("car", car);
        return "car-repair-form";
    }

    @PostMapping("/saverepair")
    public String saveRepair(@RequestParam("invoice") MultipartFile invoice, @ModelAttribute(name = "carRepair") CarRepair carRepair){

        carRepairService.save(carRepair);
        return "redirect:/showrepairs?regNumber=" + carRepair.getRegNumber();

    }

    @GetMapping("/updatecarrepair")
    public String updateCarRepair(Model model, @RequestParam("id") int id){
        CarRepair carRepair = carRepairService.getById(id);
        model.addAttribute("carRepair", carRepair);
        Car car = carService.getByRegNumber(carRepair.getRegNumber());
        model.addAttribute("car", car);
        return "car-repair-form";
    }

    @GetMapping("/deletecarrepair")
    public String deleteRepair(@RequestParam("id") Integer id){
        String regNumber = carRepairService.getById(id).getRegNumber();
        carRepairService.deleteById(id);

        return "redirect:/showrepairs?regNumber=" + regNumber;
    }

    @GetMapping("/deleteinvoice")
    public String deleteInvoice(Model model, @RequestParam int invoiceId, @RequestParam int carrepairid){
        carRepairService.deleteInvoice(invoiceId);
        return "redirect:/updatecarrepair?id="+ carrepairid;
    }

    @PostMapping("/search")
    public String search(@RequestParam(name = "search") String search, Model model){

        Car car = carService.getByRegNumber(search);
        model.addAttribute("car", car);
        return "car-form";
    }

    @GetMapping("/login")
    public String loginPage(){

        return "login";
    }




    //TODO add settings (add account etc.)
    //TODO change cardview
    //TODO Bootstrap flex




    //TODO add better searching

}
