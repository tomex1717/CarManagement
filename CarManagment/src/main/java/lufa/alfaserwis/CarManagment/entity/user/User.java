package lufa.alfaserwis.CarManagment.entity.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {


    @Id
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "enabled")
    private boolean enabled = true;
    @Transient
    private String repeatPassword;



    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "user"
    )
    private List<Authority> authorities = new ArrayList<>();

    @Transient
    private String role;

    public static final String ROLE_PREFIX = "ROLE_";



    public void addAuthority(Authority authority){

        authority.setAuthority(ROLE_PREFIX+authority.getAuthority());
        authorities.add(authority);

    }

}
