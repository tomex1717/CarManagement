package lufa.alfaserwis.CarManagment.entity.carmanagement;


import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("lat")
    @Column(name = "latitude")
    private Double latitude;

    @JsonProperty("lng")
    @Column(name = "longitude")
    private Double longitude;


    @Column(name = "timestamp")
    private Long timestamp;


//    @ManyToOne
//    private Car car;

    @Transient
    private Integer routeNumber = 0;


    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", imei=" + imei +

                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                ", routeNumber=" + routeNumber +

                '}';
    }
}
