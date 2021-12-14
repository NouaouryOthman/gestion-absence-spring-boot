package ma.karima.gestionconge.controller;

import ma.karima.gestionconge.dao.UserRepository;
import ma.karima.gestionconge.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/monProfil")
    public String profilUser(Model model) {
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) auth).getUsername();
        User user = userRepository.findByEmail(email);
        model.addAttribute("user", user);
        return "profilUser";
    }

    @GetMapping("/mesConges")
    public String mesConges() {
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) auth).getUsername();
        User user = userRepository.findByEmail(email);
        return "redirect:/conges?idUser=" + user.getId();
    }

    @GetMapping("/mesAbsences")
    public String mesAbsences() {
        Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = ((UserDetails) auth).getUsername();
        User user = userRepository.findByEmail(email);
        return "redirect:/absences?idUser=" + user.getId();
    }
}
