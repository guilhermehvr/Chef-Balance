package com.chefbalance.dao;

import com.chefbalance.factory.ConnectionFactory;
import com.chefbalance.model.TipoBebida;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoBebidaDAO {
    public void cadastrar(TipoBebida tipo) {
        String sql = "INSERT INTO tipo_bebida (nome) VALUES (?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, tipo.getNome());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    tipo.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar tipo de bebida", e);
        }
    }

    public List<TipoBebida> listarTodos() {
        String sql = "SELECT * FROM tipo_bebida";
        List<TipoBebida> tipos = new ArrayList<>();
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                TipoBebida tipo = new TipoBebida();
                tipo.setId(rs.getInt("id"));
                tipo.setNome(rs.getString("nome"));
                tipos.add(tipo);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tipos de bebida", e);
        }
        return tipos;
    }

    public TipoBebida buscarPorId(int id) {
        String sql = "SELECT * FROM tipo_bebida WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TipoBebida tipo = new TipoBebida();
                    tipo.setId(rs.getInt("id"));
                    tipo.setNome(rs.getString("nome"));
                    return tipo;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar tipo de bebida", e);
        }
        return null;
    }

    public void atualizar(TipoBebida tipo) {
        String sql = "UPDATE tipo_bebida SET nome=? WHERE id=?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipo.getNome());
            stmt.setInt(2, tipo.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar tipo de bebida", e);
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM tipo_bebida WHERE id=?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar tipo de bebida", e);
        }
    }
}