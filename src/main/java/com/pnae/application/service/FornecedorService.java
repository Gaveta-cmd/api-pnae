package com.pnae.application.service;

import com.pnae.application.dto.FornecedorRequestDTO;
import com.pnae.application.dto.FornecedorResponseDTO;
import com.pnae.domain.exception.BusinessException;
import com.pnae.domain.exception.ResourceNotFoundException;
import com.pnae.domain.model.Endereco;
import com.pnae.domain.model.Fornecedor;
import com.pnae.domain.repository.FornecedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    @Transactional(readOnly = true)
    public List<FornecedorResponseDTO> listarAtivos() {
        return fornecedorRepository.findByAtivoTrue().stream()
                .map(FornecedorResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FornecedorResponseDTO buscarPorId(Long id) {
        return FornecedorResponseDTO.from(buscarFornecedor(id));
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponseDTO> buscarPorNome(String nome) {
        return fornecedorRepository.findByRazaoSocialContainingIgnoreCase(nome).stream()
                .map(FornecedorResponseDTO::from)
                .toList();
    }

    @Transactional
    public FornecedorResponseDTO criar(FornecedorRequestDTO dto) {
        if (dto.cnpj() != null) {
            fornecedorRepository.findByCnpj(dto.cnpj()).ifPresent(f -> {
                throw new BusinessException("Já existe fornecedor com o CNPJ: " + dto.cnpj());
            });
        }
        Fornecedor fornecedor = Fornecedor.builder()
                .razaoSocial(dto.razaoSocial())
                .cnpj(dto.cnpj())
                .email(dto.email())
                .telefone(dto.telefone())
                .endereco(dto.endereco() != null ? toEndereco(dto) : null)
                .build();
        return FornecedorResponseDTO.from(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    public FornecedorResponseDTO atualizar(Long id, FornecedorRequestDTO dto) {
        Fornecedor fornecedor = buscarFornecedor(id);

        if (dto.cnpj() != null && !dto.cnpj().equals(fornecedor.getCnpj())) {
            fornecedorRepository.findByCnpj(dto.cnpj()).ifPresent(f -> {
                throw new BusinessException("Já existe fornecedor com o CNPJ: " + dto.cnpj());
            });
        }

        fornecedor.setRazaoSocial(dto.razaoSocial());
        fornecedor.setCnpj(dto.cnpj());
        fornecedor.setEmail(dto.email());
        fornecedor.setTelefone(dto.telefone());
        if (dto.endereco() != null) {
            fornecedor.setEndereco(toEndereco(dto));
        }
        return FornecedorResponseDTO.from(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    public void desativar(Long id) {
        Fornecedor fornecedor = buscarFornecedor(id);
        fornecedor.setAtivo(false);
        fornecedorRepository.save(fornecedor);
    }

    private Fornecedor buscarFornecedor(Long id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", id));
    }

    private Endereco toEndereco(FornecedorRequestDTO dto) {
        return Endereco.builder()
                .logradouro(dto.endereco().logradouro())
                .numero(dto.endereco().numero())
                .complemento(dto.endereco().complemento())
                .bairro(dto.endereco().bairro())
                .cidade(dto.endereco().cidade())
                .estado(dto.endereco().estado())
                .cep(dto.endereco().cep())
                .build();
    }
}
