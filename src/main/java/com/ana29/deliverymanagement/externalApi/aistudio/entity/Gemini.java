package com.ana29.deliverymanagement.externalApi.aistudio.entity;

import com.ana29.deliverymanagement.global.entity.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더를 통한 생성만 허용
@Builder
@Table(name = "p_gemini")
public class Gemini extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "gemini_id", columnDefinition = "uuid")
    private UUID id;

    @Column(length = 500)
    private String content;

}
