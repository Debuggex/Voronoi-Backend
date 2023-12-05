package com.voronoi.voronoiworkspace.Entities;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Customer")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Name", length = 100)
    private String username;

    @Column(name = "FirstName", length = 40)
    private String firstName;

    @Column(name = "LastName", length = 40)
    private String lastName;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "Password")
    private String password;

    @Column(name = "isSubscribed", length = 100)
    private Boolean isSubscribed = false;

    @Column(name = "isInternal")
    private Boolean isInternal = false;

    @Column(name = "isAdmin")
    private Boolean isAdmin = false;

    @Column(name = "PlainPassword")
    private String plainPassword;



    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "userId")
    private Set<Images> images = new HashSet<>();


    public User addSet(Images image) {
        image.setUserId(this);
        this.images.add(image);
        return this;
    }

}
