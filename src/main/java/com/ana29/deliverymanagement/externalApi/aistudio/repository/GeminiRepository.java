package com.ana29.deliverymanagement.externalApi.aistudio.repository;

import com.ana29.deliverymanagement.externalApi.aistudio.entity.Gemini;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GeminiRepository extends JpaRepository<Gemini, UUID> {

}
