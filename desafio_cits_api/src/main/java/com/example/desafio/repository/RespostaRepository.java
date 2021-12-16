package com.example.desafio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.desafio.model.Resposta;

@Repository
public interface RespostaRepository extends JpaRepository<Resposta, Long>{

	@Query(value = "SELECT r FROM Resposta r WHERE r.id = :id")
	Resposta buscarPorId(@Param("id") Long id);
}
