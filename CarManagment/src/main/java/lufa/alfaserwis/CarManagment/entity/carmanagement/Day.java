package lufa.alfaserwis.CarManagment.entity.carmanagement;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Day {

    private String date;
    private long maxTimestamp;
    private long minTimestamp;


    public Day() {
    }


}
