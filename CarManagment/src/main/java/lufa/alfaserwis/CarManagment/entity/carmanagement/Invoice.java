package lufa.alfaserwis.CarManagment.entity.carmanagement;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "file_name")
    private String fileName;


    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    public Invoice() {
    }


}
