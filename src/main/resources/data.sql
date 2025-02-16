-- 사용자 데이터 삽입
INSERT INTO p_users (id, nickname, email, password, phone, role, current_address, created_at, created_by)
VALUES
    ('user1', 'John Doe', 'john.doe@example.com', 'password123', '010-1234-5678', 'USER', '123 Main St', CURRENT_TIMESTAMP, 'user1'),
    ('user2', 'Jane Smith', 'jane.smith@example.com', 'password456', '010-2345-6789', 'USER', '456 Elm St', CURRENT_TIMESTAMP, 'user2'),
    ('admin', 'Admin User', 'admin@example.com', 'adminpassword', '010-3456-7890', 'ADMIN', '789 Oak St', CURRENT_TIMESTAMP, 'admin');

-- 식당 데이터 삽입
INSERT INTO p_restaurant (restaurant_id, name, content, operating_hours, created_at, created_by)
VALUES
    ('92e8d27b-d604-4be3-b0ea-b9b55f365e09', '썬니 카페', '좋은 커피와 케이크가 있는 아늑한 카페입니다.', '오전 8시 - 오후 10시', CURRENT_TIMESTAMP, 'admin'),
    ('f72ac5d3-9f1c-44f2-b676-8c02a211ac2b', '파스타 파라다이스', '신선한 재료로 만든 맛있는 파스타 요리들.', '오전 10시 - 오후 9시', CURRENT_TIMESTAMP, 'admin');


-- -- 주문 데이터 삽입
-- INSERT INTO p_order (order_id, order_status, content, operating_hours, order_request, address, price, payment_id, user_id, restaurant_id, created_at, created_by)
-- VALUES
--     ('a10b1c21-4f44-4c2f-b8fc-b1e433e4b705', 'PENDING', '치킨 주문', '11:00 - 23:00', '빠르게 배달해주세요', '서울시 강남구 테헤란로 123', 15000, '99bb48a1-1c8d-4e58-9f88-44cc0b93c78d', 'user1', '92e8d27b-d604-4be3-b0ea-b9b55f365e09', CURRENT_TIMESTAMP, 'user1'),
--     ('d6a1e5ff-d08c-470d-b09b-283dbe1b99fd', 'PENDING', '피자 주문', '10:00 - 22:00', '소스를 추가해주세요', '서울시 서초구 반포대로 456', 25000, 'b5c2290e-c2da-44f6-aad6-2b17c9be4c5f', 'user2', 'f72ac5d3-9f1c-44f2-b676-8c02a211ac2b', CURRENT_TIMESTAMP, 'user2');
--
-- ```
-- -- 사용자 데이터 삽입
-- INSERT INTO p_users (id, nickname, email, password, phone, role, current_address, created_at, created_by)
-- VALUES
--     ('user1', 'John Doe', 'john.doe@example.com', 'password123', '010-1234-5678', 'USER', '123 Main St', CURRENT_TIMESTAMP, 'user1'),
--     ('user2', 'Jane Smith', 'jane.smith@example.com', 'password456', '010-2345-6789', 'USER', '456 Elm St', CURRENT_TIMESTAMP, 'user2'),
--     ('admin', 'Admin User', 'admin@example.com', 'adminpassword', '010-3456-7890', 'ADMIN', '789 Oak St', CURRENT_TIMESTAMP, 'admin');
--
-- -- 카테고리 데이터 삽입
-- INSERT INTO p_categories (category_id, food_type) VALUES
--                                                       ('660e8400-e29b-41d4-a716-446655440000', '한식'),
--                                                       ('660e8400-e29b-41d4-a716-446655440001', '이탈리안'),
--                                                       ('660e8400-e29b-41d4-a716-446655440002', '일식');
--
-- -- 식당 데이터 삽입
-- INSERT INTO p_restaurant (restaurant_id, name, content, operating_hours, created_at, created_by)
-- VALUES
--     ('92e8d27b-d604-4be3-b0ea-b9b55f365e09', '썬니 카페', '좋은 커피와 케이크가 있는 아늑한 카페입니다.', '오전 8시 - 오후 10시', CURRENT_TIMESTAMP, 'admin'),
--     ('f72ac5d3-9f1c-44f2-b676-8c02a211ac2b', '파스타 파라다이스', '신선한 재료로 만든 맛있는 파스타 요리들.', '오전 10시 - 오후 9시', CURRENT_TIMESTAMP, 'admin');
--
-- -- 메뉴 데이터 삽입
-- INSERT INTO p_menus (menu_id, name, price, content, retaurant_id) VALUES
--                                                                       ('770e8400-e29b-41d4-a716-446655440000', '비빔밥', 10000, '신선한 야채와 고소한 고추장', '550e8400-e29b-41d4-a716-446655440000'),
--                                                                       ('770e8400-e29b-41d4-a716-446655440001', '김치찌개', 9000, '얼큰한 국물과 두부', '550e8400-e29b-41d4-a716-446655440000'),
--                                                                       ('770e8400-e29b-41d4-a716-446655440002', '마르게리따 피자', 15000, '토마토와 바질이 조화로운 피자', '550e8400-e29b-41d4-a716-446655440001'),
--                                                                       ('770e8400-e29b-41d4-a716-446655440003', '불고기 피자', 18000, '달콤한 불고기와 치즈', '550e8400-e29b-41d4-a716-446655440001'),
--                                                                       ('770e8400-e29b-41d4-a716-446655440004', '연어초밥', 12000, '신선한 연어와 와사비', '550e8400-e29b-41d4-a716-446655440002'),
--                                                                       ('770e8400-e29b-41d4-a716-446655440005', '참치초밥', 13000, '고급 참치와 간장', '550e8400-e29b-41d4-a716-446655440002');
--
-- -- 주문 데이터 삽입
-- INSERT INTO p_order (order_id, order_status, content, operating_hours, order_request, address, price, payment_id, user_id, restaurant_id, created_at, created_by)
-- VALUES
--     ('a10b1c21-4f44-4c2f-b8fc-b1e433e4b705', 'PENDING', '치킨 주문', '11:00 - 23:00', '빠르게 배달해주세요', '서울시 강남구 테헤란로 123', 15000, '99bb48a1-1c8d-4e58-9f88-44cc0b93c78d', 'user1', '92e8d27b-d604-4be3-b0ea-b9b55f365e09', CURRENT_TIMESTAMP, 'user1'),
--     ('d6a1e5ff-d08c-470d-b09b-283dbe1b99fd', 'PENDING', '피자 주문', '10:00 - 22:00', '소스를 추가해주세요', '서울시 서초구 반포대로 456', 25000, 'b5c2290e-c2da-44f6-aad6-2b17c9be4c5f', 'user2', 'f72ac5d3-9f1c-44f2-b676-8c02a211ac2b', CURRENT_TIMESTAMP, 'user2');
--
-- ```