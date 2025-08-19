package com.chefbalance.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String URL = "jdbc:postgresql://localhost:5432/chefbalance";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres"; 

    public static Connection getConnection() throws SQLException {
        try {
            // Registrar driver JDBC
            Class.forName("org.postgresql.Driver");
            
            // Estabelecer conexão
            return DriverManager.getConnection(URL, USER, PASSWORD);
            
        } catch (ClassNotFoundException e) {
            // Log detalhado do erro
            System.err.println("[ERRO] Driver JDBC não encontrado.");
            System.err.println("Certifique-se de ter o arquivo postgresql-X.X.X.jar no classpath");
            throw new SQLException("Driver JDBC do PostgreSQL não encontrado", e);
        } catch (SQLException e) {
            // Log detalhado do erro de conexão
            System.err.println("[ERRO CRÍTICO] Falha na conexão com o banco de dados");
            System.err.println("URL: " + URL);
            System.err.println("Usuário: " + USER);
            System.err.println("Mensagem do banco: " + e.getMessage());
            
            // Verificar erros comuns
            if (e.getMessage().contains("password authentication")) {
                System.err.println("ERRO: Autenticação falhou. Verifique usuário/senha.");
            } else if (e.getMessage().contains("does not exist")) {
                System.err.println("ERRO: Banco de dados 'chefbalance' não existe.");
            } else if (e.getMessage().contains("Connection refused")) {
                System.err.println("ERRO: Servidor PostgreSQL não está rodando na porta 5432");
            }
            
            throw e;
        }
    }
}