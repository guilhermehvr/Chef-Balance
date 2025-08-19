package com.chefbalance.controller;

import com.chefbalance.dao.TipoBebidaDAO;
import com.chefbalance.model.TipoBebida;
import com.google.gson.Gson;
import static spark.Spark.*;

public class TipoBebidaController {
    private static final Gson gson = new Gson();
    private static final TipoBebidaDAO tipoDAO = new TipoBebidaDAO();

    public static void setupRoutes() {
        options("/*", (req, res) -> {
            res.header("Access-Control-Allow-Headers", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            return "OK";
        });

        before((req, res) -> {
            res.type("application/json");
            res.header("Access-Control-Allow-Origin", "*");
        });

        post("/tipos", (req, res) -> {
            try {
                TipoBebida tipo = gson.fromJson(req.body(), TipoBebida.class);
                
                if (tipo.getNome() == null || tipo.getNome().isEmpty()) {
                    res.status(400);
                    return new ErrorResponse("Nome do tipo é obrigatório");
                }
                
                tipoDAO.cadastrar(tipo);
                res.status(201);
                return tipo;
            } catch (Exception e) {
                res.status(500);
                return new ErrorResponse("Erro ao cadastrar tipo: " + e.getMessage());
            }
        }, gson::toJson);

        get("/tipos", (req, res) -> {
            try {
                return tipoDAO.listarTodos();
            } catch (Exception e) {
                res.status(500);
                return new ErrorResponse("Erro ao listar tipos: " + e.getMessage());
            }
        }, gson::toJson);

        get("/tipos/:id", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                TipoBebida tipo = tipoDAO.buscarPorId(id);
                if (tipo == null) {
                    res.status(404);
                    return new ErrorResponse("Tipo não encontrado");
                }
                return tipo;
            } catch (Exception e) {
                res.status(500);
                return new ErrorResponse("Erro ao buscar tipo: " + e.getMessage());
            }
        }, gson::toJson);

        put("/tipos/:id", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                TipoBebida tipo = gson.fromJson(req.body(), TipoBebida.class);
                tipo.setId(id);
                
                if (tipo.getNome() == null || tipo.getNome().isEmpty()) {
                    res.status(400);
                    return new ErrorResponse("Nome do tipo é obrigatório");
                }
                
                tipoDAO.atualizar(tipo);
                return tipo;
            } catch (Exception e) {
                res.status(500);
                return new ErrorResponse("Erro ao atualizar tipo: " + e.getMessage());
            }
        }, gson::toJson);

        delete("/tipos/:id", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                tipoDAO.deletar(id);
                res.status(204);
                return "";
            } catch (Exception e) {
                res.status(500);
                return new ErrorResponse("Erro ao deletar tipo: " + e.getMessage());
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