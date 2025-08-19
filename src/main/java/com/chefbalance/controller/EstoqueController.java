package com.chefbalance.controller;

import com.chefbalance.dao.BebidaDAO;
import com.chefbalance.dao.MovimentacaoDAO;
import com.chefbalance.model.MovimentacaoEstoque;
import com.chefbalance.util.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;
import static spark.Spark.*;

public class EstoqueController {
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .create();
    
    private static final MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();
    private static final BebidaDAO bebidaDAO = new BebidaDAO();

    public static void setupRoutes() {
        // Registrar nova movimentação
        post("/movimentacoes", (req, res) -> {
            try {
                MovimentacaoEstoque movimentacao = gson.fromJson(req.body(), MovimentacaoEstoque.class);
                
                if (!bebidaDAO.existeBebida(movimentacao.getBebidaId())) {
                    res.status(404);
                    return new ErrorResponse("Bebida não encontrada");
                }
                
                if (movimentacao.getBebidaId() <= 0) {
                    res.status(400);
                    return new ErrorResponse("ID da bebida inválido");
                }
                if (movimentacao.getQuantidade() <= 0) {
                    res.status(400);
                    return new ErrorResponse("Quantidade deve ser maior que zero");
                }
                if (!movimentacao.getTipoMovimento().matches("VENDA|REPOSICAO|AJUSTE")) {
                    res.status(400);
                    return new ErrorResponse("Tipo de movimento inválido");
                }
                
                movimentacao.setDataHora(LocalDateTime.now());
                
                if ("VENDA".equals(movimentacao.getTipoMovimento())) {
                    int saldoAtual = movimentacaoDAO.calcularSaldoAtual(movimentacao.getBebidaId());
                    if (saldoAtual < movimentacao.getQuantidade()) {
                        res.status(400);
                        return new ErrorResponse("Estoque insuficiente. Saldo atual: " + saldoAtual);
                    }
                }
                
                movimentacaoDAO.registrarMovimentacao(movimentacao);
                res.status(201);
                return movimentacao;
                
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new ErrorResponse("Erro ao registrar movimentação: " + e.getMessage());
            }
        }, gson::toJson);

        // Obter saldo atual de uma bebida
        get("/estoque/saldo/:bebidaId", (req, res) -> {
            try {
                int bebidaId = Integer.parseInt(req.params(":bebidaId"));
                int saldo = movimentacaoDAO.calcularSaldoAtual(bebidaId);
                return new SaldoResponse(bebidaId, saldo);
            } catch (NumberFormatException e) {
                res.status(400);
                return new ErrorResponse("ID da bebida inválido");
            } catch (Exception e) {
                res.status(500);
                return new ErrorResponse("Erro ao calcular saldo: " + e.getMessage());
            }
        }, gson::toJson);

        // Listar histórico de movimentações (CORREÇÃO PARA HISTÓRICO)
        get("/movimentacoes/historico", (req, res) -> {
            try {
                String bebidaIdParam = req.queryParams("bebidaId");
                
                if (bebidaIdParam != null && !bebidaIdParam.isEmpty()) {
                    int bebidaId = Integer.parseInt(bebidaIdParam);
                    return movimentacaoDAO.buscarHistoricoPorBebida(bebidaId);
                } else {
                    // Buscar todo o histórico se nenhum ID for especificado
                    return movimentacaoDAO.buscarTodoHistorico();
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return new ErrorResponse("ID da bebida inválido");
            } catch (Exception e) {
                res.status(500);
                return new ErrorResponse("Erro ao buscar histórico: " + e.getMessage());
            }
        }, gson::toJson);
    }

    // Classes auxiliares para respostas
    private static class ErrorResponse {
        private final String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }
    
    private static class SaldoResponse {
        private final int bebidaId;
        private final int saldo;
        public SaldoResponse(int bebidaId, int saldo) {
            this.bebidaId = bebidaId;
            this.saldo = saldo;
        }
        public int getBebidaId() { return bebidaId; }
        public int getSaldo() { return saldo; }
    }
}