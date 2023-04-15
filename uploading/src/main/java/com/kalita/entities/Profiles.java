package com.kalita.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profiles {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_id_generator")
    @SequenceGenerator(name = "profile_id_generator", sequenceName = "profile_id_generator", allocationSize = 1)
    private Long id;
    private String hashPhone;
    private String hashEmail;
    private String idfa;
    private LocalDateTime proceedDateTime;
    @ManyToOne
    @JoinColumn(name = "segment_id", referencedColumnName = "id", nullable = false)
    private Segment segment;
    private UUID versionId;
}
