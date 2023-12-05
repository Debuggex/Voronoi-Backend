package com.voronoi.voronoiworkspace.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Images {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "File_Id")
    private String fileId;

    @Column(name = "Name")
    private String name;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "userId")
    private User userId;


}
