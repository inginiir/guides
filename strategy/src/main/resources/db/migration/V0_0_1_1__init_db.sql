CREATE TABLE destinations
(
    id               BIGINT NOT NULL,
    destination_name VARCHAR(100),
    CONSTRAINT segment_pk PRIMARY KEY (id)
);
create TABLE recipients
(
    id             BIGINT NOT NULL,
    recipient_name VARCHAR(100),
    destination_id BIGINT NOT NULL,
    recipient_type VARCHAR(255),
    address        VARCHAR(255),
    CONSTRAINT recipient_pk PRIMARY KEY (id),
    CONSTRAINT destination_recipient_fk FOREIGN KEY (destination_id) REFERENCES destinations (id)
);

create sequence destination_id_generator start 1;
create sequence recipient_id_generator start 1;