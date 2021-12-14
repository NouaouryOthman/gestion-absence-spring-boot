package ma.karima.gestionconge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Collection;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    @Email
    private String email;
    private String password;
    private String nom;
    private String prenom;
    @Column(unique = true)
    private String matricule;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<Role> roles;
    private boolean active;
    private int nombreConge;
    @OneToMany(mappedBy = "user")
    private Collection<Absence> absences;
    @OneToMany(mappedBy = "user")
    private Collection<Conge> conges;
    private boolean firstTimeOnline;
}
