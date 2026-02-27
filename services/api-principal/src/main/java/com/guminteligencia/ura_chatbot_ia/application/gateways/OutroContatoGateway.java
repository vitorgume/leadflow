package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.domain.TipoContato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutroContatoGateway {
    Optional<OutroContato> consultarPorNome(String nome);

    List<OutroContato> consultarPorTipo(TipoContato tipo, UUID idUsuario);

    Optional<OutroContato> consultarPorTelefoneEUsuario(String telefone, UUID idUsuario);

    OutroContato salvar(OutroContato novoOutroContato);

    Optional<OutroContato> consultarPorId(Long idOutroContato);

    Page<OutroContato> listar(Pageable pageable, UUID idUsuario);

    void deletar(Long id);
}
