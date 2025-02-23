package com.ana29.deliverymanagement.security.service;

import com.ana29.deliverymanagement.security.UserDetailsImpl;
import com.ana29.deliverymanagement.security.config.SimpleGrantedAuthorityMixin;
import com.ana29.deliverymanagement.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
@Slf4j
public class CachedUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    public CachedUserDetailsService(UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }
    //    RedisConfig 클래스의 CacheManager의 코드에서
//    redisCacheConfigurationMap.put("UserCacheStore", redisCacheConfiguration);
//     이 부분이 Cacheable 사용하는 설정. "UserCacheStore" 이 부분이 value와 같아야 함.
//       key 는 redis에서 찾아올 설정이므로 유니크한 값 아무거나 넣기.
    @Override
    @Cacheable(value = "UserCacheStore", key = "#username") // ✅ Redis 캐싱 적용
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("loadUserByUsername 메소드 실행");
        // ✅ Redis에서 캐싱된 사용자 정보 확인
        Object cachedUser = redisTemplate.opsForValue().get(username);

        if (cachedUser == null) {
            log.warn("Redis에서 캐싱된 사용자 정보 없음, DB에서 조회");
            return userRepository.findByIdAndIsDeletedFalse(username)
                    .map(UserDetailsImpl::new)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        }

        log.info("loadUserByUsername 캐싱 데이터 : " + cachedUser);

        if (cachedUser instanceof LinkedHashMap<?, ?> map) {
            log.info("loadUserByUsername 캐싱 확인");
            try {
                // ✅ LinkedHashMap을 UserDetailsImpl로 변환 (Jackson 사용)
                return objectMapper.convertValue(map, UserDetailsImpl.class);
            } catch (Exception e) {
                log.error("❌ Redis에서 UserDetails 변환 실패: {}", e.getMessage());
            }
        }

        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
    }


}