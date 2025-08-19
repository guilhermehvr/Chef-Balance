package com.chefbalance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TesteConexaoDireta {
    public static void main(String[] args) {
        // Configurações do banco (use as mesmas do seu projeto)
        String url = "jdbc:postgresql://localhost:5432/chefbalance";
        String user = "postgres";
        String password = "postgres";
        
        System.out.println("Iniciando teste direto de conexão...");
        System.out.println("URL: " + url);
        System.out.println("Usuário: " + user);
        
        try {
            // 1. Registrar o driver JDBC
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver JDBC registrado com sucesso");
            
            // 2. Estabelecer conexão
            System.out.println("Conectando ao banco de dados...");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Conexão estabelecida com sucesso!");
            
            // 3. Testar consulta ao banco
            System.out.println("Executando consulta de teste...");
            Statement stmt = conn.createStatement();
            
            // Consulta simples para verificar se a tabela bebida existe
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM bebida");
            
            if (rs.next()) {
                System.out.println("✅ Total de bebidas no banco: " + rs.getInt("total"));
            } else {
                System.out.println("⚠️ Nenhum resultado retornado");
            }
            
            // 4. Fechar conexão
            conn.close();
            System.out.println("Conexão encerrada");
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ ERRO: Driver JDBC não encontrado");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ ERRO durante a conexão:");
            e.printStackTrace();
        }
    }
}