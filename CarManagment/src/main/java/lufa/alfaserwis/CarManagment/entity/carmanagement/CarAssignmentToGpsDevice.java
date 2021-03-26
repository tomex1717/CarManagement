package lufa.alfaserwis.CarManagment.entity.carmanagement;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Table(name = "gps_devices")
@Entity
public class CarAssignmentToGpsDevice {


    @Id
    @Column(name = "car_reg_number")
    private String carRegNumber;

    @Column(name = "gps_imei")
    private String GPSImei;

    @OneToOne
    @MapsId
    @JoinColumn(name = "car_reg_number")
    private Car car;

    public CarAssignmentToGpsDevice() {
    }
}
