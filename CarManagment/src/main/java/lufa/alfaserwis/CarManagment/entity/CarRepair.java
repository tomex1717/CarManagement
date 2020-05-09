package lufa.alfaserwis.CarManagment.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "car_repairs")
public class CarRepair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "reg_number")
    private String regNumber;
    @Column(name = "mechanic")
    private String mechanic;
    @Column(name = "date")
    private String date;
    @Column(name = "total_net")
    private Integer totalNet;
    @Column(name = "total_gross")
    private Integer totalGross;
    @Column(name = "what_repaired")
    private String whatRepaired;

}
