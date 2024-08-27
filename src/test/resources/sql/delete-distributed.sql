delete from `outbound_inventory_mapping` where 1;
delete from `outbound` where 1;
delete from `inventory` where 1;

ALTER TABLE inventory ALTER COLUMN id RESTART WITH 1;

ALTER TABLE outbound ALTER COLUMN id RESTART WITH 1;

ALTER TABLE outbound_inventory_mapping ALTER COLUMN id RESTART WITH 1;
