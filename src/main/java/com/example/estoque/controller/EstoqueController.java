package com.example.estoque.controller;

import com.example.estoque.domain.Pedido;
import com.example.estoque.domain.Produto;
import com.example.estoque.exception.ForaDeEstoqueException;
import com.example.estoque.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    private final ProdutoService service;

    public EstoqueController(ProdutoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> cadastraProduto(@RequestBody Produto produto){
        service.cadastrarProduto(produto);
        return ResponseEntity.ok().body("Cadastrado com Sucesso");
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos(){
        return ResponseEntity.ok().body(service.encontrarTodos());
    }

    @GetMapping("/{nome}")
    public ResponseEntity<Produto> buscaProduto(@PathVariable String nome){
        return ResponseEntity.ok().body(service.encontrarPorNome(nome));
    }

    @PostMapping("/atualizar")
    public ResponseEntity<String> atualizarEstoque(@RequestBody Pedido pedido){
        try{
            service.atualizarEstoque(pedido);
        }catch (ForaDeEstoqueException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok().body("Estoque Atualizado");
    }
}
