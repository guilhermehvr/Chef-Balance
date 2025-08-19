package com.chefbalance.dao;

import com.chefbalance.factory.ConnectionFactory;
import com.chefbalance.model.MovimentacaoEstoque;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {

    // Registrar uma nova movimentação no estoque
    public void registrarMovimentacao(MovimentacaoEstoque movimentacao) {
        String sql = "INSERT INTO movimentacoes (bebida_id, tipo_movimento, quantidade, data_hora) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, movimentacao.getBebidaId());
            stmt.setString(2, movimentacao.getTipoMovimento());
            stmt.setInt(3, movimentacao.getQuantidade());
            stmt.setTimestamp(4, Timestamp.valueOf(movimentacao.getDataHora()));
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    movimentacao.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar movimentação de estoque", e);
        }
    }

    // Calcular saldo atual de uma bebida específica
    public int calcularSaldoAtual(int bebidaId) {
        String sql = "SELECT COALESCE(SUM(CASE WHEN tipo_movimento = 'REPOSICAO' THEN quantidade ELSE -quantidade END), 0) " +
                     "FROM movimentacoes WHERE bebida_id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bebidaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular saldo de estoque", e);
        }
        return 0;
    }

    // Buscar histórico de movimentações por bebida
    public List<MovimentacaoEstoque> buscarHistoricoPorBebida(int bebidaId) {
        String sql = "SELECT * FROM movimentacoes WHERE bebida_id = ? ORDER BY data_hora DESC";
        List<MovimentacaoEstoque> historico = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bebidaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historico.add(mapearMovimentacao(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar histórico de movimentações", e);
        }
        return historico;
    }

    // Buscar todo o histórico de movimentações (NOVO MÉTODO)
    public List<MovimentacaoEstoque> buscarTodoHistorico() {
        String sql = "SELECT * FROM movimentacoes ORDER BY data_hora DESC";
        List<MovimentacaoEstoque> historico = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                historico.add(mapearMovimentacao(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todo o histórico de movimentações", e);
        }
        return historico;
    }

    // Método auxiliar para mapear ResultSet para objeto
    private MovimentacaoEstoque mapearMovimentacao(ResultSet rs) throws SQLException {
        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setId(rs.getInt("id"));
        mov.setBebidaId(rs.getInt("bebida_id"));
        mov.setTipoMovimento(rs.getString("tipo_movimento"));
        mov.setQuantidade(rs.getInt("quantidade"));
        mov.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
        return mov;
    }

    // Buscar histórico de movimentações por período (OPCIONAL)
    public List<MovimentacaoEstoque> buscarHistoricoPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT * FROM movimentacoes WHERE data_hora BETWEEN ? AND ? ORDER BY data_hora DESC";
        List<MovimentacaoEstoque> historico = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fim));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historico.add(mapearMovimentacao(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar histórico por período", e);
        }
        return historico;
    }
}