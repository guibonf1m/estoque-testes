package com.example.estoque.repository;

import com.example.estoque.entity.ProdutoEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository repository;

    @Test
    void deveRetornarProdutoQuandoBuscarPorNome(){
        //cenário
        ProdutoEntity produto = new ProdutoEntity();
        produto.setNome("Notebook");
        produto.setDescricao("Notebook Gamer");
        produto.setPreco(4500.00);
        produto.setQtd(10);

        repository.save(produto);

        //execução
        ProdutoEntity resultado = repository.findByNome("Notebook");

        //validação
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals("Notebook", resultado.getNome());
    }
}
