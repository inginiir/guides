CREATE TABLE segment (
    id                   BIGINT NOT NULL,
    segment_name         VARCHAR(100),
    actual_version_id    UUID,
    CONSTRAINT segment_pk PRIMARY KEY (id)
);
create TABLE profiles (
    id                 BIGINT NOT NULL,
    segment_id         BIGINT,
    version_id         UUID,
    proceed_date_time  TIMESTAMP,
    idfa               VARCHAR(255),
    hash_phone         VARCHAR(255),
    hash_email         VARCHAR(255),
    CONSTRAINT profile_pk PRIMARY KEY (id),
    CONSTRAINT profile_segment_fk FOREIGN KEY (segment_id) REFERENCES segment (id)
);

create sequence segment_id_generator start 1;
create sequence profile_id_generator start 1;