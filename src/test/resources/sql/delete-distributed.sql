
delete from `outbound` where 1;
delete from `inventory` where 1;

-- INVENTORY 테이블의 ID 시퀀스를 1로 리셋
ALTER TABLE inventory ALTER COLUMN id RESTART WITH 1;

-- 필요한 경우 다른 테이블의 시퀀스도 리셋
ALTER TABLE outbound ALTER COLUMN id RESTART WITH 1;
