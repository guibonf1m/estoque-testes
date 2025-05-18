package com.example.estoque.controller;

import com.example.estoque.domain.ItemPedido;
import com.example.estoque.domain.Pedido;
import com.example.estoque.domain.Produto;
import com.example.estoque.exception.ForaDeEstoqueException;
import com.example.estoque.service.ProdutoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(controllers = EstoqueController.class)
@Import(EstoqueControllerTest.Config.class)
public class EstoqueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProdutoService service;

    @Configuration
    static class Config {
        @Bean
        public ProdutoService produtoService() {
            return Mockito.mock(ProdutoService.class);
        }

        @Bean
        public EstoqueController estoqueController() {
            return new EstoqueController(produtoService());
        }
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCadastrarProdutoComSucesso() throws Exception {
        //cenário
        Produto produto = new Produto();
        produto.setNome("Notebook");
        produto.setDescricao("Notebook Gamer");
        produto.setPreco(4500.00);
        produto.setQtd(10);

        //execução
        mockMvc.perform(
            MockMvcRequestBuilders.post("/estoque")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(produto))
        )
        //validação

        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string("Cadastrado com Sucesso"))
        .andDo(MockMvcResultHandlers.print());

        Mockito.verify(service).cadastrarProduto(Mockito.any(Produto.class));
    }

    @Test
    void deveRetornarProudutoPorNome() throws Exception {
        //cenário
        Produto produto = new Produto();
        produto.setNome("Notebook");
        produto.setDescricao("Notebook Gamer");
        produto.setPreco(4500.00);
        produto.setQtd(10);

        Mockito.when(service.encontrarPorNome("Notebook")).thenReturn(produto);

        //execução
        mockMvc.perform(MockMvcRequestBuilders.get("/estoque/Notebook"))

        //validação
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Notebook"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.descricao").value("Notebook Gamer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.preco").value(4500.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.qtd").value(10))
                .andDo(MockMvcResultHandlers.print());

        Mockito.verify(service).encontrarPorNome("Notebook");
    }

    @Test
    void deveAtualizarEstoqueComSucesso() throws Exception {
        //cenário
        ItemPedido item = new ItemPedido();
        item.setId(1l);
        item.setQtd(2);

        Pedido pedido = new Pedido();
        pedido.setItens(List.of(item));

        String json = objectMapper.writeValueAsString(pedido);

        Mockito.doNothing().when(service).atualizarEstoque(Mockito.any(Pedido.class));

        //execução + validação
        mockMvc.perform(MockMvcRequestBuilders.post("/estoque/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Estoque Atualizado"))
                .andDo(MockMvcResultHandlers.print());

        Mockito.verify(service).atualizarEstoque(Mockito.any(Pedido.class));
    }

    @Test
    void deveRetornarBadRequestQuandoEstoqueInsuficiente() throws Exception {
        //cenário
        ItemPedido item = new ItemPedido();
        item.setId(2L);
        item.setQtd(99);

        Pedido pedido = new Pedido();
        pedido.setItens(List.of(item));

        Mockito.doThrow(new ForaDeEstoqueException("Produto fora de estoque"))
                .when(service).atualizarEstoque(Mockito.any(Pedido.class));

        String json = objectMapper.writeValueAsString(pedido);

        // execução + validação
        mockMvc.perform(MockMvcRequestBuilders.post("/estoque/atualizar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Produto fora de estoque"))
                .andDo(MockMvcResultHandlers.print());

        Mockito.verify(service).atualizarEstoque(Mockito.any(Pedido.class));
    }

    @AfterEach
    void resetMocks() {
        Mockito.reset(service);
    }
}
