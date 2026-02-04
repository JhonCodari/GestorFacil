package com.JhonCodari.GestorFacil.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Embeddable
public record NomeCompleto (
    
    @NotBlank(message = "O primeiro nome não pode estar vazio")
    @Size(min = 2, max = 50, message = "O primeiro nome não pode ter menos que 2 caracteres e não pode exceder 50 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'-]+( [A-Za-zÀ-ÖØ-öø-ÿ'-]+)*$",
        message = "O primeiro nome deve conter apenas letras"
    )
    String primeiroNome,

    @NotBlank(message = "O sobrenome não pode estar vazio")
    @Size(min = 2, max = 50, message = "O sobrenome não pode ter menos que 2 caracteres e não pode exceder 50 caracteres")
    @Pattern(
        regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'-]+( [A-Za-zÀ-ÖØ-öø-ÿ'-]+)*$",
        message = "O sobrenome deve conter apenas letras"
    )
    String sobrenome
){}
