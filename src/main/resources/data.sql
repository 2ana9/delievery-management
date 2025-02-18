-- 사용자 데이터 삽입
INSERT INTO p_users (id, nickname, email, password, phone, role, current_address, created_at, created_by)
SELECT 'user1', 'johndoe123', 'john.doe@example.com', 'password123A!', '010-1234-5678', 'CUSTOMER', '123 Main St', CURRENT_TIMESTAMP, 'user1'
WHERE NOT EXISTS (SELECT 1 FROM p_users WHERE id = 'user1');

INSERT INTO p_users (id, nickname, email, password, phone, role, current_address, created_at, created_by)
SELECT 'user2', 'janesmith1', 'jane.smith@example.com', 'password456A!', '010-2345-6789', 'CUSTOMER', '456 Elm St', CURRENT_TIMESTAMP, 'user2'
WHERE NOT EXISTS (SELECT 1 FROM p_users WHERE id = 'user2');

INSERT INTO p_users (id, nickname, email, password, phone, role, current_address, created_at, created_by)
SELECT 'admin', 'adminuser1', 'admin@example.com', 'adminpass123A!', '010-3456-7890', 'MANAGER', '789 Oak St', CURRENT_TIMESTAMP, 'admin'
WHERE NOT EXISTS (SELECT 1 FROM p_users WHERE id = 'admin');

-- 카테고리 데이터 삽입
INSERT INTO p_category (category_id, food_type, is_deleted)
SELECT '660e8400-e29b-41d4-a716-446655440003', '양식', false
WHERE NOT EXISTS (SELECT 1 FROM p_category WHERE category_id = '660e8400-e29b-41d4-a716-446655440003');

INSERT INTO p_category (category_id, food_type, is_deleted)
SELECT '660e8400-e29b-41d4-a716-446655440004', '피자', false
WHERE NOT EXISTS (SELECT 1 FROM p_category WHERE category_id = '660e8400-e29b-41d4-a716-446655440004');

INSERT INTO p_category (category_id, food_type, is_deleted)
SELECT '660e8400-e29b-41d4-a716-446655440005', '치킨', false
WHERE NOT EXISTS (SELECT 1 FROM p_category WHERE category_id = '660e8400-e29b-41d4-a716-446655440005');

-- 주소 데이터 삽입
INSERT INTO p_area (area_id,city,district,town,lot_main_no,lot_sub_no,road_name,building_main_no,building_sub_no)
SELECT '747e8484-8239-497d-84d6-cb9f763e1707','서울특별시','종로구','청운동','50','6','자하문로','115','14'
    WHERE NOT EXISTS (SELECT 1 FROM p_area WHERE area_id = '747e8484-8239-497d-84d6-cb9f763e1707');

-- 식당 데이터 삽입
INSERT INTO p_restaurant (restaurant_id, name, owner_id, area_id, category_id, content, operating_hours, created_at, created_by, is_deleted)
SELECT '92e8d27b-d604-4be3-b0ea-b9b55f365e09', '썬니 카페','user1','747e8484-8239-497d-84d6-cb9f763e1707','660e8400-e29b-41d4-a716-446655440003', '좋은 커피와 케이크가 있는 아늑한 카페입니다.', '오전 8시 - 오후 10시', CURRENT_TIMESTAMP, 'admin', false
WHERE NOT EXISTS (SELECT 1 FROM p_restaurant WHERE restaurant_id = '92e8d27b-d604-4be3-b0ea-b9b55f365e09');

INSERT INTO p_restaurant (restaurant_id, name, owner_id, area_id, category_id, content, operating_hours, created_at, created_by, is_deleted)
SELECT 'f72ac5d3-9f1c-44f2-b676-8c02a211ac2b', '파스타 파라다이스','user2','747e8484-8239-497d-84d6-cb9f763e1707','660e8400-e29b-41d4-a716-446655440003', '신선한 재료로 만든 맛있는 파스타 요리들.', '오전 10시 - 오후 9시', CURRENT_TIMESTAMP, 'admin', false
WHERE NOT EXISTS (SELECT 1 FROM p_restaurant WHERE restaurant_id = 'f72ac5d3-9f1c-44f2-b676-8c02a211ac2b');

-- 메뉴 데이터 삽입
INSERT INTO p_menus (menu_id, name, price, content, restaurant_id)
SELECT '770e8400-e29b-41d4-a716-446655440000', '비빔밥', 10000, '신선한 야채와 고소한 고추장', '92e8d27b-d604-4be3-b0ea-b9b55f365e09'
WHERE NOT EXISTS (SELECT 1 FROM p_menus WHERE menu_id = '770e8400-e29b-41d4-a716-446655440000');

INSERT INTO p_menus (menu_id, name, price, content, restaurant_id)
SELECT '770e8400-e29b-41d4-a716-446655440001', '김치찌개', 9000, '얼큰한 국물과 두부', '92e8d27b-d604-4be3-b0ea-b9b55f365e09'
WHERE NOT EXISTS (SELECT 1 FROM p_menus WHERE menu_id = '770e8400-e29b-41d4-a716-446655440001');