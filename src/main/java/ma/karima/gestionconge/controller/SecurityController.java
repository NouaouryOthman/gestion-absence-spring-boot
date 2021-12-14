package ma.karima.gestionconge.controller;

import ma.karima.gestionconge.dao.UserRepository;
import ma.karima.gestionconge.entity.Role;
import ma.karima.gestionconge.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Configuration
@Controller
public class SecurityController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index() {
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) auth).getUsername();
        User user = userRepository.findByEmail(email);
        if (user.isFirstTimeOnline())
            return "redirect:/passwordChange?id=" + user.getId();
        if (user.getRoles().contains(new Role(1L, "ADMIN")))
            return "redirect:/users";
        return "redirect:/monProfil";
    }

    @GetMapping("/passwordChange")
    public String change(Model model, Long id) {
        model.addAttribute("id", id);
        model.addAttribute("password", new String());
        return "passwordChange";
    }

    @PostMapping("/password")
    public String password(@ModelAttribute("password") String password, Long id) {
        ma.karima.gestionconge.entity.User user = userRepository.findById(id).get();
        PasswordEncoder pe = passwordEncoder();
        user.setPassword(pe.encode(password));
        user.setFirstTimeOnline(false);
        userRepository.save(user);
        return "redirect:/";
    }

    @GetMapping(path = "/login")
    public String login() {
        return "login";
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/login";
    }

    @GetMapping("/403")
    public String nonAutorise() {
        return "403";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }
}