package com.pnae.domain.repository;

import com.pnae.domain.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    List<Fornecedor> findByAtivoTrue();

    Optional<Fornecedor> findByCnpj(String cnpj);

    List<Fornecedor> findByRazaoSocialContainingIgnoreCase(String razaoSocial);
}
