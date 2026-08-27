package br.com.ifsc.agenda360.database.repository;

import br.com.ifsc.agenda360.database.model.ConsultaEntity;
import br.com.ifsc.agenda360.database.model.enums.StatusConsulta;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IConsultaRepository extends JpaRepository<ConsultaEntity, UUID> {

    @Modifying
    @Transactional
    @Query("UPDATE ConsultaEntity c SET c.status = :status WHERE c.id = :id")
    int atualizarStatus(@Param("id") UUID id, @Param("status") StatusConsulta status);

    List<ConsultaEntity> findByStatus(StatusConsulta status);
}
