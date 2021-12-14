package ma.karima.gestionconge.controller;

import ma.karima.gestionconge.dao.AbsenceRepository;
import ma.karima.gestionconge.dao.CongeRepository;
import ma.karima.gestionconge.dao.RoleRepository;
import ma.karima.gestionconge.dao.UserRepository;
import ma.karima.gestionconge.entity.Absence;
import ma.karima.gestionconge.entity.Conge;
import ma.karima.gestionconge.entity.Role;
import ma.karima.gestionconge.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Configuration
@Controller
public class AdminController {

    @Autowired
    AbsenceRepository absenceRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CongeRepository congeRepository;

    @GetMapping("/users")
    public String users(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "5") int size,
                        @RequestParam(name = "motCle", defaultValue = "") String motCle) {
        Page<User> pageUsers = userRepository.findByMatriculeContains(motCle, PageRequest.of(page, size));
        model.addAttribute("users", pageUsers.getContent());
        model.addAttribute("pages", new int[pageUsers.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("motCle", motCle);
        model.addAttribute("size", size);
        return "users";
    }

    @GetMapping(path = "/supprimerUser")
    public String supprimerUser(Long id, int page, int size, String motCle) {
        userRepository.deleteById(id);
        return "redirect:/users?page=" + page + "&size=" + size + "&motCle" + motCle;
    }

    @GetMapping(path = "/activerUser")
    public String activerUser(Long id, int page, int size, String motCle) {
        User user = userRepository.findById(id).get();
        if (user.isActive())
            user.setActive(false);
        else
            user.setActive(true);
        userRepository.save(user);
        return "redirect:/users?page=" + page + "&size=" + size + "&motCle" + motCle;
    }

    @GetMapping("/nouveauEmploye")
    public String formEmploye(Model model) {
        model.addAttribute("user", new User());
        return "formEmploye";
    }

    @PostMapping("/saveEmploye")
    public String saveEmploye(Model model, @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "formEmploye";
        PasswordEncoder pe = pEncoder();
        user.setPassword(pe.encode(user.getPassword()));
        Role role = roleRepository.findByName("EMPLOYE");
        Collection<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);
        user.setActive(true);
        user.setFirstTimeOnline(true);
        userRepository.save(user);
        model.addAttribute("user", user);
        return "redirect:/";
    }

    @GetMapping("/absent")
    public String absent(Long id, int page, int size, String motCle) {
        User user = userRepository.findById(id).get();
        user.setNombreConge(user.getNombreConge() - 1);
        userRepository.save(user);
        Absence absence = new Absence();
        absence.setJustifie(false);
        absence.setDate(new Date());
        absence.setUser(user);
        absenceRepository.save(absence);
        return "redirect:/users?page=" + page + "&size=" + size + "&motCle" + motCle;
    }

    @GetMapping("/absences")
    public String absences(Model model, @RequestParam(name = "idUser") Long idUser) {
        Collection<Absence> absences = userRepository.findById(idUser).get().getAbsences();
        model.addAttribute("absences", absences);
        model.addAttribute("id", idUser);
        return "absences";
    }

    @GetMapping("/absenceJustifiee")
    public String absenceJustifiee(@RequestParam(name = "idAbsence") Long idAbsence) {
        Absence absence = absenceRepository.findById(idAbsence).get();
        Long id = absence.getUser().getId();
        User user = userRepository.findById(id).get();
        if (absence.isJustifie()) {
            absence.setJustifie(false);
            user.setNombreConge(user.getNombreConge() - 1);
        } else {
            absence.setJustifie(true);
            user.setNombreConge(user.getNombreConge() + 1);
        }
        userRepository.save(user);
        absenceRepository.save(absence);
        return "redirect:/absences?idUser=" + id;
    }

    @GetMapping("/supprimerAbsence")
    public String suprimerAbsence(Long idAbsence, Long idUser) {
        if (!absenceRepository.getById(idAbsence).isJustifie()) {
            User user = userRepository.findById(idUser).get();
            user.setNombreConge(user.getNombreConge() + 1);
            userRepository.save(user);
        }
        absenceRepository.deleteById(idAbsence);
        return "redirect:/absences?idUser=" + idUser;
    }


    @GetMapping("/conges")
    public String conges(Model model, @RequestParam(name = "idUser") Long idUser) {
        Collection<Conge> conges = userRepository.findById(idUser).get().getConges();
        model.addAttribute("conges", conges);
        model.addAttribute("id", idUser);
        return "conges";
    }

    @GetMapping("/ajouterConge")
    public String ajouterConge(Model model, @RequestParam(name = "idUser") Long id) {
        model.addAttribute("conge", new Conge());
        model.addAttribute("idUser", id);
        return "formConge";
    }

    @PostMapping("saveConge")
    public String saveConge(Long id, @Valid Conge conge) {
        Date datedebut = conge.getDebutConge();
        Date datefin = conge.getFinConge();
        Conge conge1 = new Conge();
        conge1.setFinConge(datefin);
        conge1.setDebutConge(datedebut);
        User user = userRepository.findById(id).get();
        conge1.setUser(user);
        long jours = ChronoUnit.DAYS.between(conge.getDebutConge().toInstant(), conge.getFinConge().toInstant());
        if (user.getNombreConge() < jours)
            return "formConge";
        user.setNombreConge(user.getNombreConge() - (int) jours);
        conge.setUser(user);
        userRepository.save(user);
        congeRepository.save(conge1);
        return "redirect:/conges?idUser=" + id;
    }

    @Bean
    protected PasswordEncoder pEncoder() {
        return new BCryptPasswordEncoder();
    }

}
