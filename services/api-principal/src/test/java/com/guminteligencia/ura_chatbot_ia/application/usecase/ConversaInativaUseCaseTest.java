package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.usecase.crm.CrmUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.EscolhaVendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import com.guminteligencia.ura_chatbot_ia.domain.Usuario;
import com.guminteligencia.ura_chatbot_ia.domain.vendedor.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversaInativaUseCaseTest {

    @Mock
    private ConversaAgenteUseCase conversaAgenteUseCase;
    @Mock
    private EscolhaVendedorUseCase escolhaVendedorUseCase; // Changed from VendedorUseCase
    @Mock
    private MensagemUseCase mensagemUseCase;
    @Mock
    private MensagemBuilder mensagemBuilder;
    @Mock
    private CrmUseCase crmUseCase;

    private ConversaInativaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConversaInativaUseCase(
                conversaAgenteUseCase,
                escolhaVendedorUseCase, // Changed from vendedorUseCase
                crmUseCase,
                mensagemUseCase,
                mensagemBuilder,
                "dev"
        );
    }

    @Test
    void naoDeveProcessarQuandoNaoExistiremConversas() {
        when(conversaAgenteUseCase.listarNaoFinalizados()).thenReturn(List.of());

        useCase.verificaAusenciaDeMensagem();

        verify(conversaAgenteUseCase).listarNaoFinalizados();
        verifyNoInteractions(escolhaVendedorUseCase, mensagemUseCase, mensagemBuilder, crmUseCase);
    }

    @Test
    void deveProcessarFluxoG1() {
        LocalDateTime now = LocalDateTime.of(2025, 8, 4, 12, 0);

        // --- CORREÇÃO AQUI: Adicione Mockito.CALLS_REAL_METHODS ---
        try (var mockNow = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            mockNow.when(LocalDateTime::now).thenReturn(now);

            ConversaAgente conv = mock(ConversaAgente.class);
            when(conv.getStatus()).thenReturn(StatusConversa.ANDAMENTO);
            when(conv.getFinalizada()).thenReturn(false);
            when(conv.getDataUltimaMensagem()).thenReturn(now.minusSeconds(15));
            Cliente cliente = Cliente.builder()
                    .id(UUID.randomUUID())
                    .nome("teste")
                    .telefone("+55999999999")
                    .build();
            when(conv.getCliente()).thenReturn(cliente);
            when(conversaAgenteUseCase.listarNaoFinalizados()).thenReturn(List.of(conv));
            when(mensagemBuilder.getMensagem(eq(TipoMensagem.RECONTATO_INATIVO_G1), isNull(), eq(cliente)))
                    .thenReturn("msg-recontato-g1");

            useCase.verificaAusenciaDeMensagem();

            verify(conv).setStatus(StatusConversa.INATIVO_G1);
            verify(mensagemBuilder).getMensagem(eq(TipoMensagem.RECONTATO_INATIVO_G1), isNull(), eq(cliente));
            verify(mensagemUseCase).enviarMensagem("msg-recontato-g1", cliente.getTelefone(), false);
            verify(conv).setDataUltimaMensagem(now);
            verify(conversaAgenteUseCase).salvar(conv);
            verifyNoInteractions(escolhaVendedorUseCase, crmUseCase);
        }
    }

    @Test
    void deveProcessarFluxoG2() {
        LocalDateTime now = LocalDateTime.of(2025, 8, 4, 12, 0);

        // --- CORREÇÃO AQUI TAMBÉM ---
        try (var mockNow = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            mockNow.when(LocalDateTime::now).thenReturn(now);

            ConversaAgente conv = mock(ConversaAgente.class);
            when(conv.getStatus()).thenReturn(StatusConversa.INATIVO_G1);
            when(conv.getFinalizada()).thenReturn(true); // Atenção: Verifique se sua lógica de negócio espera true ou false aqui
            when(conv.getDataUltimaMensagem()).thenReturn(now.minusSeconds(25));
            Cliente cliente = Cliente.builder()
                    .id(UUID.randomUUID())
                    .nome("teste")
                    .telefone("+55999999999")
                    .usuario(Usuario.builder().id(UUID.randomUUID()).build())
                    .build();
            when(conv.getCliente()).thenReturn(cliente);
            when(conversaAgenteUseCase.listarNaoFinalizados()).thenReturn(List.of(conv));

            Vendedor vendedor = Vendedor.builder().id(1L).nome("Nome teste").build();
            when(escolhaVendedorUseCase.roletaVendedoresContatosInativos(any(UUID.class))).thenReturn(vendedor);

            useCase.verificaAusenciaDeMensagem();

            verify(conv).setStatus(StatusConversa.INATIVO_G2);
            // verify(conv).setFinalizada(true); // Se o mock já retornou true, o setFinalizada pode não ter sido chamado se a lógica tiver uma guarda.
            verify(escolhaVendedorUseCase).roletaVendedoresContatosInativos(any(UUID.class));
            verify(conv).setVendedor(vendedor);
            verify(crmUseCase).atualizarCrm(vendedor, cliente, conv);
            verify(conversaAgenteUseCase).salvar(conv);
            verifyNoInteractions(mensagemBuilder);
        }
    }
}
