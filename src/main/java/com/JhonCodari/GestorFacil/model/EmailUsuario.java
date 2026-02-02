package com.JhonCodari.GestorFacil.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Embeddable
public record EmailUsuario(
    @NotBlank(message = "O email não pode estar em branco.")
    @Size(max = 100, message = "O email não pode exceder 100 caracteres.")
    @Email(message = "O email dever ser um endereco valido")
    String valor
) {}

// TODO: Implementar confirmação de e-mail após cadastro.
// Fluxo sugerido:
// 1. Ao cadastrar o usuário, salvar com status "não verificado".
// 2. Gerar um token único e enviar e-mail de confirmação para o endereço informado.
// 3. Criar endpoint para receber o token e confirmar o e-mail (ex: /usuarios/confirmar-email?token=...).
// 4. Ao confirmar, atualizar o status do usuário para "verificado".
// 5. Impedir login e operações sensíveis para usuários não verificados.
// 6. Utilizar Spring Boot Starter Mail ou serviço externo para envio dos e-mails.
