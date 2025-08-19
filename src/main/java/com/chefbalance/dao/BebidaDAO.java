package com.chefbalance.dao;

import com.chefbalance.factory.ConnectionFactory;
import com.chefbalance.model.Bebida;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BebidaDAO {
    // CADASTRO CORRIGIDO
    public void cadastrar(Bebida bebida) {
        String sql = "INSERT INTO bebida (nome, tipo, preco_custo, preco_venda) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, bebida.getNome());
            stmt.setString(2, bebida.getTipo());
            stmt.setDouble(3, bebida.getPrecoCusto());
            stmt.setDouble(4, bebida.getPrecoVenda());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    bebida.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar bebida", e);
        }
    }

    // ATUALIZAÇÃO CORRIGIDA
    public void atualizar(Bebida bebida) {
        String sql = "UPDATE bebida SET nome=?, tipo=?, preco_custo=?, preco_venda=? WHERE id=?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bebida.getNome());
            stmt.setString(2, bebida.getTipo());
            stmt.setDouble(3, bebida.getPrecoCusto());
            stmt.setDouble(4, bebida.getPrecoVenda());
            stmt.setInt(5, bebida.getId()); // ÍNDICE CORRIGIDO
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar bebida", e);
        }
    }

    // MÉTODO ADICIONADO PARA VERIFICAÇÃO DE BEBIDA
    public boolean existeBebida(int bebidaId) {
        String sql = "SELECT COUNT(*) FROM bebida WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bebidaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência da bebida", e);
        }
    }

    // MÉTODOS EXISTENTES (MANTIDOS)
    public List<Bebida> listarTodos() {
        String sql = "SELECT id, nome, tipo, preco_custo, preco_venda FROM bebida";
        List<Bebida> bebidas = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Bebida bebida = new Bebida();
                bebida.setId(rs.getInt("id"));
                bebida.setNome(rs.getString("nome"));
                bebida.setTipo(rs.getString("tipo"));
                bebida.setPrecoCusto(rs.getDouble("preco_custo"));
                bebida.setPrecoVenda(rs.getDouble("preco_venda"));
                bebidas.add(bebida);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar bebidas", e);
        }
        return bebidas;
    }

    public Bebida buscarPorId(int id) {
        String sql = "SELECT id, nome, tipo, preco_custo, preco_venda FROM bebida WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Bebida bebida = new Bebida();
                    bebida.setId(rs.getInt("id"));
                    bebida.setNome(rs.getString("nome"));
                    bebida.setTipo(rs.getString("tipo"));
                    bebida.setPrecoCusto(rs.getDouble("preco_custo"));
                    bebida.setPrecoVenda(rs.getDouble("preco_venda"));
                    return bebida;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar bebida", e);
        }
        return null;
    }

    public void deletar(int id) {
        String sql = "DELETE FROM bebida WHERE id=?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar bebida", e);
        }
    }
}