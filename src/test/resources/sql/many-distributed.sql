INSERT INTO inventory (goods_id, goods_name, location, location_type, quantity)
VALUES ('G1', '사과', 'L1', '보관1', 1),
       ('G1', '사과', 'L1', '보관2', 1),
       ('G1', '사과', 'L1', '보관3', 1);

INSERT INTO outbound (quantity, remain_quantity, outbound_status)
VALUES (3, 3,'READY');

INSERT INTO outbound_inventory_mapping(outbound_id, inventory_id, process)
VALUES (1, 1, false),
       (1, 2, false),
       (1, 3, false);
