package lufa.alfaserwis.CarManagment.service;

import lufa.alfaserwis.CarManagment.dao.carmanagement.CarRepository;
import lufa.alfaserwis.CarManagment.entity.carmanagement.Car;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepositoryMock;


    @BeforeAll
    public void beforeAll() {
        MockitoAnnotations.initMocks(this);

    }


    @Test
    void getAllShouldReturnListNotNull() {
        List<Car> carListToBeReturned = carRepositoryMock.findByOrderByRegNumberAsc();
        Assertions.assertNotNull(carListToBeReturned);

    }


    @Test
    void getByRegNumber() {
    }

    @Test
    void save() {
    }

    @Test
    void deleteById() {
    }
}