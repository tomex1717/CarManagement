package lufa.alfaserwis.CarManagment.controller;

import lufa.alfaserwis.CarManagment.entity.user.Authority;
import lufa.alfaserwis.CarManagment.entity.user.User;
import lufa.alfaserwis.CarManagment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/adduser")
    public String addUser(Model model){

        User user = new User();
        model.addAttribute("user", user);
        return "add-user";
    }

    @PostMapping("/adduser")
    public String addAndSaveUser(@ModelAttribute(name = "user") User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Authority authority = new Authority();
        authority.setUsername(user.getUsername());
        authority.setAuthority(user.getRole());
        user.addAuthority(authority);
        userService.saveUser(user);

        return "redirect:/";
    }


    @GetMapping("/resetpassword")
    public String resetPassword(Model model){
        List<User> userList = userService.getAllUsers();

        model.addAttribute("users", userList);

        return "reset-password";
    }

    @PostMapping("/resetpassword")
    public String resetPassword(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password){
        User user = userService.findByUserName(username);
        user.setPassword(passwordEncoder.encode(password));
        userService.saveUser(user);


        return "redirect:/";
    }
}
