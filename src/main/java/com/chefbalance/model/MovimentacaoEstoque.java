package com.chefbalance.model;

import java.time.LocalDateTime;

public class MovimentacaoEstoque {
    private int id;
    private int bebidaId;
    private String tipoMovimento; // "VENDA", "REPOSICAO", "AJUSTE"
    private int quantidade;
    private LocalDateTime dataHora;

    // Construtor padrão
    public MovimentacaoEstoque() {}

    // Construtor com campos
    public MovimentacaoEstoque(int bebidaId, String tipoMovimento, int quantidade) {
        this.bebidaId = bebidaId;
        this.tipoMovimento = tipoMovimento;
        this.quantidade = quantidade;
        this.dataHora = LocalDateTime.now();
    }

    // Getters e Setters (validações removidas)
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    public int getBebidaId() { 
        return bebidaId; 
    }
    
    public void setBebidaId(int bebidaId) { 
        this.bebidaId = bebidaId; 
    }
    
    public String getTipoMovimento() { 
        return tipoMovimento; 
    }
    
    public void setTipoMovimento(String tipoMovimento) { 
        this.tipoMovimento = tipoMovimento; 
    }
    
    public int getQuantidade() { 
        return quantidade; 
    }
    
    public void setQuantidade(int quantidade) { 
        this.quantidade = quantidade; 
    }
    
    public LocalDateTime getDataHora() { 
        return dataHora; 
    }
    
    public void setDataHora(LocalDateTime dataHora) { 
        this.dataHora = dataHora; 
    }
}