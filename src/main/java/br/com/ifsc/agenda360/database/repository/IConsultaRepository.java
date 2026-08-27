package br.com.ifsc.agenda360.database.repository;

import br.com.ifsc.agenda360.database.model.ConsultaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IConsultaRepository extends JpaRepository<ConsultaEntity, UUID> {
}
