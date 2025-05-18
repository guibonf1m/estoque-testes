package com.example.estoque.service;

import com.example.estoque.domain.ItemPedido;
import com.example.estoque.domain.Pedido;
import com.example.estoque.domain.Produto;
import com.example.estoque.entity.ProdutoEntity;
import com.example.estoque.exception.ForaDeEstoqueException;
import com.example.estoque.repository.ProdutoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoService service;

    @Test
    void DeveRetornarProdutoQuandoNomeExistir() {
        //cenário
        ProdutoEntity produto = new ProdutoEntity();
        produto.setNome("Notebook");
        produto.setDescricao("Notebook Gamer");
        produto.setPreco(4500.00);
        produto.setQtd(10);

        Mockito.when(repository.findByNome("Notebook")).thenReturn(produto);

        //execução
        Produto reultado = service.encontrarPorNome("Notebook");

        //validação
        Assertions.assertEquals("Notebook", reultado.getNome());
        Assertions.assertEquals("Notebook Gamer", reultado.getDescricao());
        Assertions.assertEquals(4500.00, reultado.getPreco());
        Assertions.assertEquals(10, reultado.getQtd());

    }

    @Test
    void deveRetornaListaDeProdutos(){
        //cenário
        ProdutoEntity produto1 = new ProdutoEntity();
        produto1.setNome("Notebook");
        produto1.setDescricao("Notebook Gamer");
        produto1.setPreco(4500.00);
        produto1.setQtd(10);

        ProdutoEntity produto2 = new ProdutoEntity();
        produto2.setNome("Mouse");
        produto2.setDescricao("Mouse Gamer");
        produto2.setPreco(100.00);
        produto2.setQtd(20);

        List<ProdutoEntity> produtos = List.of(produto1, produto2);
        Mockito.when(repository.findAll()).thenReturn(produtos);

        //execução
        List<Produto> result = service.encontrarTodos();

        //validação
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Notebook", result.get(0).getNome());
        Assertions.assertEquals("Mouse", result.get(1).getNome());
    }

    @Test
    void deveAtualizarEstoqueQuandoQuantidadeSuficiente() {
        //cenário
        ProdutoEntity produto = new ProdutoEntity();
        produto.setId(1L);
        produto.setNome("Teclado");
        produto.setQtd(10);

        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setId(1L);
        itemPedido.setQtd(3);

        Pedido pedido = new Pedido();
        pedido.setItens(List.of(itemPedido));

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(produto));

        //execução
        service.atualizarEstoque(pedido);

        //validação
        Assertions.assertEquals(7, produto.getQtd());
        Mockito.verify(repository).save(produto);
    }

     @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente(){
        //cenário
        ProdutoEntity produto = new ProdutoEntity();
        produto.setId(1L);
        produto.setNome("Monitor");
        produto.setQtd(2);

        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setId(1L);
        itemPedido.setQtd(5);

        Pedido pedido = new Pedido();
        pedido.setItens(List.of(itemPedido));

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(produto));

        //execução + validação

         ForaDeEstoqueException excecao = Assertions.assertThrows(
                 ForaDeEstoqueException.class,
                 () -> service.atualizarEstoque(pedido)
         );

         Assertions.assertTrue(excecao.getMessage().contains("Monitor"));
     }

    @Test
    void deveAtualizarProdutoExistente(){
        //cenário
        Produto produto = new Produto();
        produto.setNome("Monitor");
        produto.setQtd(30);

        ProdutoEntity existente = new ProdutoEntity();
        existente.setNome("Monitor");
        existente.setQtd(10);

        Mockito.when(repository.findByNome("Monitor")).thenReturn(existente);

        //execução
        service.cadastrarProduto(produto);

        //validação
        Assertions.assertEquals(30, existente.getQtd());
        Mockito.verify(repository).save(existente);
    }

    @Test
    void deveCadastrarProdutoQuandoNaoExistir(){
        //cenário
        Produto produto = new Produto();
        produto.setNome("Monitor");
        produto.setDescricao("Monitor muito bom");
        produto.setPreco(1000.00);
        produto.setQtd(5);

        Mockito.when(repository.findByNome("Monitor")).thenReturn(null);

        //execução
        service.cadastrarProduto(produto);

        //validação

        ArgumentCaptor<ProdutoEntity> captor = ArgumentCaptor.forClass(ProdutoEntity.class);
        Mockito.verify(repository).save(captor.capture());

        ProdutoEntity salvo = captor.getValue();
        Assertions.assertEquals("Monitor", salvo.getNome());
        Assertions.assertEquals("Monitor muito bom", salvo.getDescricao());
        Assertions.assertEquals(1000.00, salvo.getPreco());
        Assertions.assertEquals(5, salvo.getQtd());
    }
}
