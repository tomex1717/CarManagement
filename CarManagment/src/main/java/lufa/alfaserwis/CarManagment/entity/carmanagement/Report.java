package lufa.alfaserwis.CarManagment.entity.carmanagement;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "reports")
@Getter
@Setter
public class Report implements Serializable {

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

    @Column(name = "engine_rpm")
    private Integer engineRPM = 0;


    @Column(name = "timestamp")
    private Long timestamp;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            insertable = false,
            updatable = false,
            name = "imei",
            referencedColumnName = "gps_imei"
    )
    private CarAssignmentToGpsDevice gps;

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
