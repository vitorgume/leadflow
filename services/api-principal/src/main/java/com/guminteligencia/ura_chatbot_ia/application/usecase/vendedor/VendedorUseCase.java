package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorComMesmoTelefoneException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.VendedorGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.UsuarioUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendedorUseCase {

    private final VendedorGateway gateway;
    private final UsuarioUseCase usuarioUseCase;

    public Vendedor cadastrar(Vendedor novoVendedor) {
        log.info("Cadastrando novo vendedor. Novo vendedor: {}", novoVendedor);

        Optional<Vendedor> vendedor = gateway.consultarPorTelefone(novoVendedor.getTelefone());

        if(vendedor.isPresent() && vendedor.get().getTelefone().equals(novoVendedor.getTelefone())) {
            throw new VendedorComMesmoTelefoneException();
        }

        Usuario usuario = usuarioUseCase.consultarPorId(novoVendedor.getUsuario().getId());
        novoVendedor.setUsuario(usuario);

        novoVendedor = gateway.salvar(novoVendedor);

        log.info("Novo vendedor cadastrado com sucesso. Vendedor: {}", novoVendedor);

        return novoVendedor;
    }

    public Vendedor consultarVendedor(String nome) {
        Optional<Vendedor> vendedor = gateway.consultarVendedor(nome);

        if (vendedor.isEmpty()) {
            throw new VendedorNaoEncontradoException();
        }

        return vendedor.get();
    }

    public Vendedor alterar(Vendedor novosDados, Long idVendedor) {
        log.info("Alterando dados do vendedor. Novos dados: {}, Id vendedor: {}", novosDados, idVendedor);

        Vendedor vendedor = this.consultarPorId(idVendedor);

        vendedor.setDados(novosDados);

        vendedor = gateway.salvar(vendedor);

        log.info("Alteração de novos dados concluida com sucesso. Novos dados: {}", vendedor);

        return vendedor;
    }

    public List<Vendedor> listarPorUsuario(UUID idUsuario) {
        log.info("Listando vendedores.");

        List<Vendedor> vendedores = gateway.listarPorUsuario(idUsuario);

        log.info("Vendedores listados com sucesso. Vendedores: {}", vendedores);

        return vendedores;
    }

    public void deletar(Long idVendedor) {
        log.info("Deletando vendedor. Id vendedor: {}", idVendedor);

        this.consultarPorId(idVendedor);
        gateway.deletar(idVendedor);

        log.info("Vendedor deletado com sucesso.");
    }

    public Vendedor consultarPorId(Long idVendedor) {
        log.info("Consultando vendedor pelo id. Id vendedor: {}", idVendedor);
        Optional<Vendedor> vendedor = gateway.consultarPorId(idVendedor);

        if (vendedor.isEmpty()) {
            throw new VendedorNaoEncontradoException();
        }

        return vendedor.get();
    }

    public Vendedor consultarVendedorPadrao() {
        Optional<Vendedor> vendedor = gateway.consultarVendedorPadrao();

        if(vendedor.isEmpty()) {
            throw new VendedorNaoEncontradoException();
        }

        return vendedor.get();
    }
}
