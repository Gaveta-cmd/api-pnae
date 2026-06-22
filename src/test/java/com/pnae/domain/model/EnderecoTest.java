package com.pnae.domain.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EnderecoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private Endereco enderecoValido() {
        return Endereco.builder()
                .logradouro("Rua das Flores")
                .numero("100")
                .bairro("Centro")
                .cidade("São Paulo")
                .estado(UF.SP)
                .cep("01310-100")
                .build();
    }

    @Test
    void enderecoValido_semViolacoes() {
        Set<ConstraintViolation<Endereco>> violations = validator.validate(enderecoValido());
        assertTrue(violations.isEmpty());
    }

    @Test
    void logradouroVazio_deveGerarViolacao() {
        Endereco endereco = Endereco.builder()
                .logradouro("").numero("100").bairro("Centro")
                .cidade("São Paulo").estado(UF.SP).cep("01310-100").build();
        Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("logradouro")));
    }

    @Test
    void logradouroNulo_deveGerarViolacao() {
        Endereco endereco = Endereco.builder()
                .numero("100").cidade("São Paulo").estado(UF.SP).cep("01310-100").build();
        Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);
        assertFalse(violations.isEmpty());
    }

    @Test
    void cidadeVazia_deveGerarViolacao() {
        Endereco endereco = Endereco.builder()
                .logradouro("Rua A").numero("1").cidade("").estado(UF.SP).cep("01310-100").build();
        Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cidade")));
    }

    @Test
    void estadoNulo_deveGerarViolacao() {
        Endereco endereco = Endereco.builder()
                .logradouro("Rua A").cidade("São Paulo").cep("01310-100").build();
        Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);
        assertFalse(violations.isEmpty());
    }

    @Test
    void cepFormatoInvalido_deveGerarViolacao() {
        Endereco endereco = Endereco.builder()
                .logradouro("Rua A").cidade("SP").estado(UF.SP).cep("12345678").build();
        Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cep")));
    }

    @Test
    void cepNulo_deveGerarViolacao() {
        Endereco endereco = Endereco.builder()
                .logradouro("Rua A").cidade("SP").estado(UF.SP).build();
        Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);
        assertFalse(violations.isEmpty());
    }

    @Test
    void complementoNulo_naoDeveGerarViolacao() {
        Endereco endereco = Endereco.builder()
                .logradouro("Rua A").cidade("SP").estado(UF.SP).cep("01310-100").build();
        Set<ConstraintViolation<Endereco>> violations = validator.validate(endereco);
        assertTrue(violations.isEmpty());
    }
}
