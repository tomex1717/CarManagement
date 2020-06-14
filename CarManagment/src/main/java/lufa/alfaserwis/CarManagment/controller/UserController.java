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
        if(!user.getPassword().equals(user.getRepeatPassword())){
            return "redirect:/user/adduser?mismatch";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Authority authority = new Authority();
        authority.setUser(user);
        authority.setAuthority(user.getRole());
        user.addAuthority(authority);
        userService.saveUser(user);

        return "redirect:/user/adduser?success";
    }


    @GetMapping("/resetpassword")
    public String resetPassword(Model model){
        List<User> userList = userService.getAllUsers();
        model.addAttribute("users", userList);

        return "reset-password";
    }

    @PostMapping("/resetpassword")
    public String resetPassword(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password, @RequestParam(name = "repeatpassword") String repeatpassword) {
        if (!password.equals(repeatpassword)) {
            return "redirect:/user/resetpassword?mismatch";
        }
        User user = userService.findByUserName(username);
        user.setPassword(passwordEncoder.encode(password));
        userService.saveUser(user);

        return "redirect:/user/resetpassword?success";
    }

    @GetMapping("/deleteuser")
    public String deleteUser(Model model) {
        List<User> userList = userService.getAllUsers();
        model.addAttribute("users", userList);

        return "delete-user";
    }

    @PostMapping("/deleteuser")
    public String deleteUser(@RequestParam(name = "username") String username) {

        User user = userService.findByUserName(username);
        userService.deleteUser(user);

        return "redirect:/user/deleteuser?success";
    }


}
