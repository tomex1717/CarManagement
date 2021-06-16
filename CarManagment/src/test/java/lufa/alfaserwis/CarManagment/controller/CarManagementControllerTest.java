package lufa.alfaserwis.CarManagment.controller;

import lufa.alfaserwis.CarManagment.entity.carmanagement.Car;
import lufa.alfaserwis.CarManagment.service.CarRepairService;
import lufa.alfaserwis.CarManagment.service.CarService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarManagementControllerTest {

    @Mock
    private CarService carService;
    @Mock
    private CarRepairService carRepairService;

    @BeforeAll
    public void beforeAll() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    void SaveRepairShouldInvokeSaveMethod() {
        Car car = new Car();
        carService.save(car);
        Mockito.verify(carService, Mockito.times(1)).save(car);
    }


    @Test
    void deleteRepairShouldInvokeDeleteMethod() {
        Car car = new Car();
        carService.deleteById(car.getRegNumber());
        Mockito.verify(carService, Mockito.times(1)).deleteById(car.getRegNumber());
    }

    @Test
    void deleteInvoiceShouldInvokeDeleteInvoiceMethod() {
        carRepairService.deleteInvoice(0);
        Mockito.verify(carRepairService, Mockito.times(1)).deleteInvoice(0);
    }


}