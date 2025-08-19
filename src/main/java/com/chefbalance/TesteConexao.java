package com.chefbalance;

import com.chefbalance.factory.ConnectionFactory;
import java.sql.Connection;

public class TesteConexao {
    public static void main(String[] args) {
        System.out.println("Iniciando teste de conexão...");
        
        try (Connection connection = ConnectionFactory.getConnection()) {
            System.out.println(" Conexão com o PostgreSQL estabelecida com sucesso!");
            System.out.println("Detalhes da conexão:");
            System.out.println("Banco de dados: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("Versão: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("URL: " + connection.getMetaData().getURL());
        } catch (Exception e) {
            System.err.println(" Falha na conexão!");
            System.err.println("Motivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}