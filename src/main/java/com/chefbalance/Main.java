package com.chefbalance;

import com.chefbalance.controller.BebidaController;
import com.chefbalance.controller.EstoqueController;
import com.chefbalance.controller.TipoBebidaController;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        // Configuração básica
        port(8000);
        
        // Configuração de arquivos estáticos
        staticFiles.externalLocation(System.getProperty("user.dir") + "/frontend");
        
        // Configuração de CORS (Essencial para o frontend)
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            res.type("application/json");
        });
        
        options("/*", (req, res) -> {
            return "OK";
        });

        // Mensagem de inicialização
        printBanner();
        
        // Rotas
        BebidaController.setupRoutes();
        TipoBebidaController.setupRoutes();
        EstoqueController.setupRoutes(); // Adicionado o controller de estoque
        
        // Gerenciamento de erros
        configureExceptionHandling();
    }
    
    private static void printBanner() {
        String banner = """
        ===========================================
        🚀 Servidor Chef Balance iniciado na porta 8000
        🔗 Endpoints:
        POST   /bebidas       - Cadastrar nova bebida
        GET    /bebidas       - Listar todas
        GET    /bebidas/:id   - Buscar por ID
        PUT    /bebidas/:id   - Atualizar
        DELETE /bebidas/:id   - Remover
        
        POST   /movimentacoes - Registrar movimentação de estoque
        GET    /estoque/saldo/:bebidaId - Obter saldo atual
        GET    /movimentacoes/historico - Obter histórico de movimentações
        
        🌐 Frontend: http://localhost:8000/index.html
        ===========================================
        """;
        System.out.println(banner);
    }
    
    private static void configureExceptionHandling() {
        exception(Exception.class, (e, req, res) -> {
            System.err.println("[ERRO] " + e.getMessage());
            res.status(500);
            res.body("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        });
        
        notFound((req, res) -> {
            res.type("application/json");
            return "{\"error\":\"Endpoint não encontrado: " + req.pathInfo() + "\"}";
        });
    }
} //ejdgit 


//testando no vs
