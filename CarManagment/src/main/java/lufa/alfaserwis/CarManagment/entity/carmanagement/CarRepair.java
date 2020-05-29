package lufa.alfaserwis.CarManagment.entity.carmanagement;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "car_repairs")
public class CarRepair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
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

    @OneToMany(cascade = {CascadeType.ALL
                })
    @JoinColumn(name = "car_repair_id",nullable = false)
    private List<Invoice> invoices = new ArrayList<>();

    @Transient
    private MultipartFile invoice;



    public void addInvoice(Invoice invoice){
        invoices.add(invoice);
    }
}
