package com.chefbalance.controller;

import com.chefbalance.dao.BebidaDAO;
import com.chefbalance.model.Bebida;
import com.google.gson.Gson;
import static spark.Spark.*;

public class BebidaController {
    private static final Gson gson = new Gson();
    private static final BebidaDAO bebidaDAO = new BebidaDAO();

    public static void setupRoutes() {
        System.out.println("Configurando rotas para /bebidas...");
        
        post("/bebidas", (req, res) -> {
            System.out.println("Recebida requisição POST para /bebidas");
            try {
                Bebida bebida = gson.fromJson(req.body(), Bebida.class);
                
                if (bebida.getNome() == null || bebida.getNome().isEmpty()) {
                    res.status(400);
                    return new ErrorResponse("Nome é obrigatório");
                }
                
                if (bebida.getTipo() == null || bebida.getTipo().isEmpty()) {
                    res.status(400);
                    return new ErrorResponse("Tipo é obrigatório");
                }
                
                if (bebida.getPrecoCusto() <= 0) {
                    res.status(400);
                    return new ErrorResponse("Preço de custo deve ser maior que zero");
                }
                
                if (bebida.getPrecoVenda() <= 0) {
                    res.status(400);
                    return new ErrorResponse("Preço de venda deve ser maior que zero");
                }
                
                
                
                bebidaDAO.cadastrar(bebida);
                res.status(201);
                return bebida;
            } catch (Exception e) {
                System.err.println("Erro ao cadastrar: " + e.getMessage());
                res.status(500);
                return new ErrorResponse("Erro ao cadastrar: " + e.getMessage());
            }
        }, gson::toJson);

        get("/bebidas", (req, res) -> {
            System.out.println("Recebida requisição GET para /bebidas");
            try {
                return bebidaDAO.listarTodos();
            } catch (Exception e) {
                System.err.println("Erro ao listar bebidas: " + e.getMessage());
                res.status(500);
                return new ErrorResponse("Erro ao listar: " + e.getMessage());
            }
        }, gson::toJson);

        get("/bebidas/:id", (req, res) -> {
            System.out.println("Recebida requisição GET para /bebidas/" + req.params(":id"));
            try {
                int id = Integer.parseInt(req.params(":id"));
                Bebida bebida = bebidaDAO.buscarPorId(id);
                if (bebida == null) {
                    res.status(404);
                    return new ErrorResponse("Bebida não encontrada");
                }
                return bebida;
            } catch (Exception e) {
                System.err.println("Erro ao buscar bebida: " + e.getMessage());
                res.status(500);
                return new ErrorResponse("Erro ao buscar: " + e.getMessage());
            }
        }, gson::toJson);

        put("/bebidas/:id", (req, res) -> {
            System.out.println("Recebida requisição PUT para /bebidas/" + req.params(":id"));
            try {
                int id = Integer.parseInt(req.params(":id"));
                Bebida bebida = gson.fromJson(req.body(), Bebida.class);
                bebida.setId(id);
                
                if (bebida.getNome() == null || bebida.getNome().isEmpty()) {
                    res.status(400);
                    return new ErrorResponse("Nome é obrigatório");
                }
                
                if (bebida.getTipo() == null || bebida.getTipo().isEmpty()) {
                    res.status(400);
                    return new ErrorResponse("Tipo é obrigatório");
                }
                
                if (bebida.getPrecoCusto() <= 0) {
                    res.status(400);
                    return new ErrorResponse("Preço de custo deve ser maior que zero");
                }
                
                if (bebida.getPrecoVenda() <= 0) {
                    res.status(400);
                    return new ErrorResponse("Preço de venda deve ser maior que zero");
                }
                
                
                
                bebidaDAO.atualizar(bebida);
                return bebida;
            } catch (Exception e) {
                System.err.println("Erro ao atualizar bebida: " + e.getMessage());
                res.status(500);
                return new ErrorResponse("Erro ao atualizar: " + e.getMessage());
            }
        }, gson::toJson);

        delete("/bebidas/:id", (req, res) -> {
            System.out.println("Recebida requisição DELETE para /bebidas/" + req.params(":id"));
            try {
                int id = Integer.parseInt(req.params(":id"));
                bebidaDAO.deletar(id);
                res.status(204);
                return "";
            } catch (Exception e) {
                System.err.println("Erro ao deletar bebida: " + e.getMessage());
                res.status(500);
                return new ErrorResponse("Erro ao deletar: " + e.getMessage());
            }
        }, gson::toJson);
    }

    private static class ErrorResponse {
        private final String error;
        public ErrorResponse(String error) {
            this.error = error;
        }
        public String getError() {
            return error;
        }
    }
}