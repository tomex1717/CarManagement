package lufa.alfaserwis.CarManagment.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "cars")
public class Car {


    @Id
    @Column(name = "reg_number")
    private String regNumber;

    @Column(name = "mark")
    private String mark;
    @Column(name = "model")
    private String model;
    @Column(name = "color")
    private String color;
    @Column(name = "fuel_card_expire")
    private String fuelCardExpire;
    @Column(name = "city")
    private String city;
    @Column(name = "user")
    private String user;
    @Column(name = "nickname")
    private String nickname;
    @Column(name = "fuel_card")
    private Boolean fuelCard;
    @Column(name = "overview_date")
    private String overviewDate;
    @Column(name = "oil_millage")
    private Integer oilMillage;
    @Column(name = "total_millage")
    private Integer totalMillage;
    @Column(name = "fuel_type")
    private String fuelType;
    @Column(name = "gas_cylinder_expire")
    private String gasCylinderExpire;
    @Column(name = "gas_overview_millage")
    private Integer gasOverviewMillage;
    @Column(name = "first_reg_date")
    private String firstRegDate;
    @Column(name = "insurer")
    private String insurer;
    @Column(name = "oc_date")
    private String OcDate;
    @Column(name = "oc_company")
    private String OcCompany;
    @Column(name = "ac_company")
    private String AcCompany;
    @Column(name = "assistant")
    private String assistant;
    @Column(name = "vat_1")
    private Boolean VAT_1;
    @Column(name = "summer_tires_size")
    private String summerTiresSize;
    @Column(name = "winter_tires_size")
    private String winterTiresSize;
    @Column(name = "tires_localization")
    private String tiresLocalization;
    @Column(name = "equipment")
    private String equipment;
    @Column(name = "other")
    private String other;
    @Column(name="oil_date")
    private String oilDate;
    @Column(name = "gas_overview_date")
    private String gasOverviewDate;
    @Column(name = "overview_millage")
    private Integer overviewMillage;

    @Transient
    private MultipartFile carPic;

    @Transient
    private String carPicPath;

    @Transient
    private String photoDir;

    public Car() {

    }
}
