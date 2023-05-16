package com.kalita.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "recipients")
@AllArgsConstructor
@NoArgsConstructor
public class Recipient {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipient_id_generator")
    @SequenceGenerator(name = "recipient_id_generator", sequenceName = "recipient_id_generator", allocationSize = 1)
    private Long id;
    @Column(name = "recipient_name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "destination_id", referencedColumnName = "id")
    private Destination destination;
    @Column(name = "recipient_type")
    @Enumerated(EnumType.STRING)
    private RecipientType type;
    private String address;
}
