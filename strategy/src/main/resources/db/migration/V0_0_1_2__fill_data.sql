insert into destinations(id, destination_name)
values (nextval('destination_id_generator'), 'Sales department'),
       (nextval('destination_id_generator'), 'Development department');

insert into recipients(id, recipient_name, destination_id, recipient_type, address)
values (nextval('recipient_id_generator'), 'Nick', (select id from destinations where destination_name = 'Sales department'), 'PHONE', '777-77-77'),
       (nextval('recipient_id_generator'), 'John', (select id from destinations where destination_name = 'Sales department'), 'PHONE', '777-77-78'),
       (nextval('recipient_id_generator'), 'Theresa', (select id from destinations where destination_name = 'Sales department'), 'TELEGRAM', '-739180421'),
       (nextval('recipient_id_generator'), 'Michael', (select id from destinations where destination_name = 'Sales department'), 'EMAIL', 'manager@company.com'),
       (nextval('recipient_id_generator'), 'Kristina', (select id from destinations where destination_name = 'Development department'), 'TELEGRAM', '-739180429'),
       (nextval('recipient_id_generator'), 'Denis', (select id from destinations where destination_name = 'Development department'), 'EMAIL', 'lead@company.com');
