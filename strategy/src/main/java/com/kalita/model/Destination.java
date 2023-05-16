package com.kalita.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "destinations")
@AllArgsConstructor
@NoArgsConstructor
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "destination_id_generator")
    @SequenceGenerator(name = "destination_id_generator", sequenceName = "destination_id_generator", allocationSize = 1)
    private Long id;
    @Column(name = "destination_name")
    private String name;
    @OneToMany(mappedBy = "destination", fetch = FetchType.EAGER)
    private Set<Recipient> recipients;
}
