package lufa.alfaserwis.CarManagment.entity.carmanagement;


import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;

@Entity
@Table(name = "reports")
@Getter
@Setter
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "reg_number")
    private String regNumber;
    @Column(name = "imei")
    private Long imei;
    @Column(name = "speed")
    private double speed;
    @Column(name = "altitude")
    private double altitude;
    @Column(name = "longitude")
    private double longitude;
    @Column(name = "latitude")
    private double latitude;
    @Column(name = "timestamp")
    private long timestamp;


}
