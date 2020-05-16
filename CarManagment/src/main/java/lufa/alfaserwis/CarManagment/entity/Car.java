package lufa.alfaserwis.CarManagment.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "cars")
public class Car {


    @Id
    @Column(name = "reg_number")
    @Size(min=2, max=10, message = "min 2, max 10 znaków")
    private String regNumber;

    @Size(min=2, max=20, message = "min 2, max 20 znaków")
    @Column(name = "mark")
    private String mark;

    @Size(min=2, max=20, message = "min 2, max 20 znaków")
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

    @Min(value = 0, message = "wartość musi być powyżej 0")
    @Column(name = "oil_millage")
    private Integer oilMillage;
    @Min(value = 0, message = "wartość musi być >=0")
    @Column(name = "total_millage")
    private Integer totalMillage;


    @Column(name = "fuel_type")
    private String fuelType;


    @Column(name = "gas_cylinder_expire")
    private String gasCylinderExpire;

    @Min(value = 0, message = "wartość musi być >=0")
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

    @Min(value = 0, message = "wartość musi być >=0")
    @Column(name = "overview_millage")
    private Integer overviewMillage;


    @Column(name = "pic_name")
    private String carPicName;
    @Column(name = "pic_path")
    private String carPicPath;
    @Transient
    private MultipartFile carPic;

    @Transient
    private boolean isNew = false;

//TODO add validation to every field




    public Car() {

    }

}
