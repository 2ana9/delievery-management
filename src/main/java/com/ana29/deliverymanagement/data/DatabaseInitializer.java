package com.ana29.deliverymanagement.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    private static final int TOTAL_USERS = 300;        // 생성할 사용자 수
    private static final int TOTAL_RESTAURANTS = 100;  // 생성할 식당 수
    private static final int MENUS_PER_RESTAURANT = 5; // 각 식당당 메뉴 개수
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        insertCategories();  // 🔹 카테고리 데이터 먼저 삽입
        insertAreas();       // 외래 키 문제 방지: 먼저 지역 데이터 삽입
        insertUsers();       // 사용자 데이터 삽입
        insertRestaurants(); // 식당 데이터 삽입
        insertMenus();       // 메뉴 데이터 삽입
    }

    private void insertUsers() {
        for (int i = 2; i <= TOTAL_USERS; i++) {
            String phone = "010-" + (1000 + (int) (Math.random() * 9000)) + "-" + (1000 + (int) (Math.random() * 9000));

            entityManager.createNativeQuery(
                            "INSERT INTO p_users (id, nickname, email, password, phone, role, created_at, created_by) " +
                                    "SELECT :id, :nickname, :email, :password, :phone, 'CUSTOMER', CURRENT_TIMESTAMP, :createdBy " +
                                    "WHERE NOT EXISTS (SELECT 1 FROM p_users WHERE id = :id)"
                    )
                    .setParameter("id", "user" + i)
                    .setParameter("nickname", "nick" + i)
                    .setParameter("email", "user" + i + "@example.com")
                    .setParameter("password", passwordEncoder.encode(String.format("Password%d@!",i)))
                    .setParameter("phone", phone)
                    .setParameter("createdBy", "user" + i)
                    .executeUpdate();
        }
    }

    private void insertAreas() {
        for (int i = 1; i <= 5; i++) {
            String city = switch (i) {
                case 1 -> "서울특별시";
                case 2 -> "부산광역시";
                case 3 -> "대구광역시";
                case 4 -> "인천광역시";
                case 5 -> "광주광역시";
                default -> "기타";
            };

            String district = (i % 2 == 0) ? "중구" : "서구"; // 예제용 랜덤 데이터
            String town = "법정동 " + i;
            String road = "도로명 " + i;

            entityManager.createNativeQuery(
                            "INSERT INTO p_area (area_id, city, district, town, road) " +
                                    "SELECT :areaId, :city, :district, :town, :road " +
                                    "WHERE NOT EXISTS (SELECT 1 FROM p_area WHERE area_id = :areaId)"
                    )
                    .setParameter("areaId", i)
                    .setParameter("city", city)  // 🔹 올바른 컬럼명 적용
                    .setParameter("district", district)
                    .setParameter("town", town)
                    .setParameter("road", road)
                    .executeUpdate();
        }
    }

    private void insertCategories() {
        String[][] categories = {
                {"660e8400-e29b-41d4-a716-446655440003", "카페"},
                {"770e8400-e29b-41d4-a716-446655440004", "한식"},
                {"880e8400-e29b-41d4-a716-446655440005", "양식"},
                {"990e8400-e29b-41d4-a716-446655440006", "중식"},
                {"aaa8400-e29b-41d4-a716-446655440007", "일식"}
        };

        for (String[] category : categories) {
            UUID categoryId = UUID.fromString(category[0]);
            String foodType = category[1];

            entityManager.createNativeQuery(
                            "INSERT INTO p_category (category_id, food_type, is_deleted) " +
                                    "SELECT :categoryId, :foodType, :isDeleted " +
                                    "WHERE NOT EXISTS (SELECT 1 FROM p_category WHERE category_id = :categoryId)"
                    )
                    .setParameter("categoryId", categoryId)
                    .setParameter("foodType", foodType)
                    .setParameter("isDeleted", false) // 🔹 명시적으로 `false` 설정
                    .executeUpdate();
        }
    }


    private void insertRestaurants() {
        for (int i = 1; i <= TOTAL_RESTAURANTS; i++) {
            UUID restaurantId = UUID.randomUUID(); // UUID 직접 생성
            String ownerId = "user" + (2 + (i % (TOTAL_USERS - 1))); // user2 ~ user(TOTAL_USERS) 중 랜덤 선택
            UUID categoryId = UUID.fromString("660e8400-e29b-41d4-a716-446655440003"); // UUID 타입으로 변환
            int areaId = 1 + (i % 5); // 1~5 지역 랜덤 배정
            String name = "레스토랑 " + i;
            String content = "맛있는 음식을 제공하는 " + name;
            String operatingHours = "오전 10시 - 오후 10시";

            entityManager.createNativeQuery(
                            "INSERT INTO p_restaurant (restaurant_id, name, owner_id, area_id, category_id, content, operating_hours, created_at, created_by, is_deleted) " +
                                    "SELECT CAST(:restaurantId AS UUID), :name, :ownerId, :areaId, :categoryId, :content, :operatingHours, CURRENT_TIMESTAMP, 'admin', false " +
                                    "WHERE NOT EXISTS (SELECT 1 FROM p_restaurant WHERE restaurant_id = CAST(:restaurantId AS UUID))"
                    )
                    .setParameter("restaurantId", restaurantId) // UUID 객체 직접 전달
                    .setParameter("name", name)
                    .setParameter("ownerId", ownerId)
                    .setParameter("areaId", areaId)
                    .setParameter("categoryId", categoryId)
                    .setParameter("content", content)
                    .setParameter("operatingHours", operatingHours)
                    .executeUpdate();
        }
    }


    private void insertMenus() {
        for (int i = 1; i <= TOTAL_RESTAURANTS; i++) {
            // 🔹 restaurant_id를 UUID로 가져오기
            UUID restaurantId = (UUID) entityManager.createNativeQuery(
                            "SELECT restaurant_id FROM p_restaurant ORDER BY RANDOM() LIMIT 1")
                    .getSingleResult(); // 🔹 UUID 타입으로 직접 가져오기

            for (int j = 1; j <= MENUS_PER_RESTAURANT; j++) {
                UUID menuId = UUID.randomUUID();
                String name = "메뉴 " + j + " - 레스토랑 " + i;
                int price = 5000 + (j * 1000);
                String content = "맛있는 " + name;

                entityManager.createNativeQuery(
                                "INSERT INTO p_menus (menu_id, name, price, content, restaurant_id) " +
                                        "SELECT :menuId, :name, :price, :content, :restaurantId " +
                                        "WHERE NOT EXISTS (SELECT 1 FROM p_menus WHERE menu_id = :menuId)"
                        )
                        .setParameter("menuId", menuId) // 🔹 UUID 사용
                        .setParameter("name", name)
                        .setParameter("price", price)
                        .setParameter("content", content)
                        .setParameter("restaurantId", restaurantId) // 🔹 UUID 사용
                        .executeUpdate();
            }
        }
    }

}
