package lufa.alfaserwis.CarManagment.entity.carmanagement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GPSElementBinaryCoding {
    private float longitude;
    private float latitude;
    private int altitude;
    private int angle;
    private int satellites;
    private int speed;


    @Override
    public String toString() {
        return "GPSElementBinaryCoding{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", altitude=" + altitude +
                ", angle=" + angle +
                ", satellites=" + satellites +
                ", speed=" + speed +
                '}';
    }
}
