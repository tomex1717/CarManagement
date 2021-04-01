package lufa.alfaserwis.CarManagment.entity.carmanagement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Table(name = "gps_devices")
@Entity
public class CarAssignmentToGpsDevice implements Serializable {


    @Id
    @Column(name = "car_reg_number")
    private String carRegNumber;


    @Column(name = "gps_imei")
    private String GPSImei;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "car_reg_number")
    private Car car;

    public CarAssignmentToGpsDevice() {
    }


}
