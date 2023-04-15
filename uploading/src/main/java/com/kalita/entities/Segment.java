package com.kalita.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
public class Segment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "segment_id_generator")
    @SequenceGenerator(name = "segment_id_generator", sequenceName = "segment_id_generator", allocationSize = 1)
    private Long id;
    private String segmentName;
    @OneToMany(mappedBy = "segment", fetch = FetchType.LAZY)
    private List<Profiles> profiles;
    private UUID actualVersionId;
}
