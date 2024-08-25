INSERT INTO inventory (goods_id, goods_name, location, location_type, quantity)
VALUES ('G1', '사과', 'L1', '보관', 10);

INSERT INTO outbound (inventory_id, quantity, outbound_status)
VALUES (1, 1, 'READY');


