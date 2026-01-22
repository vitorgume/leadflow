package com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.NenhumVendedorReferenciadoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorComMesmoTelefoneException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.VendedorNaoEscolhidoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.VendedorGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConfiguracaoEscolhaVendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.condicoes.CondicaoComposite;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.condicoes.CondicaoType;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Condicao;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConectorLogico;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.ConfiguracaoEscolhaVendedor;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendedorUseCase {

    private final VendedorGateway gateway;
    private final Random random = new Random();
    private static String ultimoVendedor = null;
    private final ConfiguracaoEscolhaVendedorUseCase configuracaoEscolhaVendedorUseCase;
    private final CondicaoComposite condicaoComposite;

    public Vendedor cadastrar(Vendedor novoVendedor) {
        log.info("Cadastrando novo vendedor. Novo vendedor: {}", novoVendedor);

        Optional<Vendedor> vendedor = gateway.consultarPorTelefone(novoVendedor.getTelefone());

        if(vendedor.isPresent() && vendedor.get().getTelefone().equals(novoVendedor.getTelefone())) {
            throw new VendedorComMesmoTelefoneException();
        }

        novoVendedor = gateway.salvar(novoVendedor);

        log.info("Novo vendedor cadastrado com sucesso. Vendedor: {}", novoVendedor);

        return novoVendedor;
    }


    public Vendedor escolherVendedor(Cliente cliente) {
        List<ConfiguracaoEscolhaVendedor> configuracoes = configuracaoEscolhaVendedorUseCase.listarPorUsuario(cliente.getUsuario().getId());

        for (ConfiguracaoEscolhaVendedor configuracao : configuracoes) {
            if (avaliarCondicoes(cliente, configuracao.getCondicoes())) {

                if(configuracao.getVendedores().size() == 1) {
                    log.info("Vendedor {} escolhido para o cliente {} com base na configuração {}",
                            configuracao.getVendedores().getFirst().getNome(), cliente.getNome(), configuracao.getId());

                    return configuracao.getVendedores().getFirst();
                } else if (configuracao.getVendedores().size() > 1) {
                    Vendedor vendedor = this.roletaVendedores(configuracao.getVendedores());
                    log.info("Vendedor {} escolhido para o cliente {} com base na configuração {}",
                            vendedor.getNome(), cliente.getNome(), configuracao.getId());

                    return vendedor;
                } else {
                    throw new NenhumVendedorReferenciadoException();
                }

            }
        }

        log.warn("Nenhum vendedor foi escolhido para o cliente {}. Nenhuma configuração correspondeu.", cliente.getNome());
        throw new VendedorNaoEscolhidoException();
    }

    private boolean avaliarCondicoes(Cliente cliente, List<Condicao> condicoes) {
        if (condicoes == null || condicoes.isEmpty()) {
            return false;
        }

        // 1. Avaliar todas as condições individualmente
        List<Boolean> resultados = new ArrayList<>();
        for (Condicao condicao : condicoes) {
            CondicaoType condicaoType = condicaoComposite.escolher(condicao.getOperadorLogico());
            resultados.add(condicaoType.executar(cliente, condicao));
        }

        // 2. Processar conectores AND (têm precedência)
        List<Boolean> resultadosPosAnd = new ArrayList<>();
        List<ConectorLogico> conectoresPosOr = new ArrayList<>();

        if (!resultados.isEmpty()) {
            boolean acumuladorAnd = resultados.get(0);

            for (int i = 0; i < condicoes.size() - 1; i++) {
                ConectorLogico conector = condicoes.get(i).getConectorLogico();
                boolean proximoResultado = resultados.get(i + 1);

                if (conector == ConectorLogico.AND) {
                    acumuladorAnd = acumuladorAnd && proximoResultado;
                } else { // OR
                    resultadosPosAnd.add(acumuladorAnd);
                    conectoresPosOr.add(ConectorLogico.OR);
                    acumuladorAnd = proximoResultado;
                }
            }
            resultadosPosAnd.add(acumuladorAnd);
        } else {
            return false; // Nenhuma condição para avaliar
        }

        // 3. Processar conectores OR
        boolean resultadoFinal = false;
        if (!resultadosPosAnd.isEmpty()) {
            resultadoFinal = resultadosPosAnd.get(0);
            for (int i = 1; i < resultadosPosAnd.size(); i++) {
                // Neste ponto, todos os conectores restantes em conectoresPosOr são OR
                resultadoFinal = resultadoFinal || resultadosPosAnd.get(i);
            }
        }

        return resultadoFinal;
    }

    public synchronized Vendedor roletaVendedores(List<Vendedor> vendedores) {
        // 1. Filtra vendedores inativos
        List<Vendedor> vendedoresAtivos = vendedores.stream()
                .filter(v -> !v.getInativo())
                .toList();

        // 2. Se não houver vendedores ativos, lança uma exceção
        if (vendedoresAtivos.isEmpty()) {
            throw new NenhumVendedorReferenciadoException();
        }

        // 3. Se houver apenas um vendedor ativo, retorna ele
        if (vendedoresAtivos.size() == 1) {
            Vendedor unicoVendedor = vendedoresAtivos.get(0);
            ultimoVendedor = unicoVendedor.getNome();
            return unicoVendedor;
        }

        // 4. Tenta encontrar vendedores que não sejam o último escolhido
        List<Vendedor> possiveisVendedores = vendedoresAtivos.stream()
                .filter(v -> !v.getNome().equals(ultimoVendedor))
                .toList();

        // 5. Se todos os vendedores ativos foram o último, volta a considerar todos os ativos
        if (possiveisVendedores.isEmpty()) {
            possiveisVendedores = vendedoresAtivos;
        }

        // 6. Sorteia um vendedor da lista de candidatos
        Vendedor vendedorEscolhido = possiveisVendedores.get(random.nextInt(possiveisVendedores.size()));
        ultimoVendedor = vendedorEscolhido.getNome();
        return vendedorEscolhido;
    }


    public synchronized Vendedor roletaVendedoresContatosInativos() {
        List<Vendedor> vendedores = gateway.listar();

        // 1. Filtra vendedores inativos
        List<Vendedor> vendedoresAtivos = vendedores.stream()
                .filter(v -> !v.getInativo())
                .toList();

        // 2. Se não houver vendedores ativos, lança uma exceção
        if (vendedoresAtivos.isEmpty()) {
            throw new NenhumVendedorReferenciadoException();
        }

        // 3. Se houver apenas um vendedor ativo, retorna ele
        if (vendedoresAtivos.size() == 1) {
            Vendedor unicoVendedor = vendedoresAtivos.get(0);
            ultimoVendedor = unicoVendedor.getNome();
            return unicoVendedor;
        }

        // 4. Tenta encontrar vendedores que não sejam o último escolhido
        List<Vendedor> possiveisVendedores = vendedoresAtivos.stream()
                .filter(v -> !v.getNome().equals(ultimoVendedor))
                .toList();

        // 5. Se todos os vendedores ativos foram o último, volta a considerar todos os ativos
        if (possiveisVendedores.isEmpty()) {
            possiveisVendedores = vendedoresAtivos;
        }

        // 6. Sorteia um vendedor da lista de candidatos
        Vendedor vendedorEscolhido = possiveisVendedores.get(random.nextInt(possiveisVendedores.size()));
        ultimoVendedor = vendedorEscolhido.getNome();
        return vendedorEscolhido;
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

    public List<Vendedor> listar() {
        log.info("Listando vendedores.");

        List<Vendedor> vendedores = gateway.listar();

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
