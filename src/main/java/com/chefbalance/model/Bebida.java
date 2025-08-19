package com.chefbalance.model;

public class Bebida {
    private int id;
    private String nome;
    private String tipo;
    private double precoCusto;
    private double precoVenda;
    

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public double getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(double precoCusto) { this.precoCusto = precoCusto; }
    
    public double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(double precoVenda) { this.precoVenda = precoVenda; }
    
   
}