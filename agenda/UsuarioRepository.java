package com.devcaotics.agenda.repositories;

import com.devcaotics.agenda.model.Usuario;
import java.sql.*;

import org.springframework.stereotype.Repository;

@Repository
public final class UsuarioRepository implements RepositorioGenerico<Usuario, Integer> {

    protected UsuarioRepository() {}

    @Override
    public void create(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO Usuario (nome, username, senha, telefone, rua, cidade, estado) VALUES(?,?,?,?,?,?,?)";

        try (Connection conn = GerenciadorConexao.obterConexao()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstm.setString(1, usuario.getNome());
                pstm.setString(2, usuario.getUsername());
                pstm.setString(3, usuario.getPassword());
                pstm.setString(4, usuario.getTelefone());
                pstm.setString(5, usuario.getRua());
                pstm.setString(6, usuario.getCidade());
                pstm.setString(7, usuario.getEstado());
                pstm.executeUpdate();

                try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getInt(1));
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public void update(Usuario usuario) throws SQLException {
        String sql = "UPDATE Usuario SET nome=?, username=?, senha=?, telefone=?, rua=?, cidade=?, estado=? WHERE id_usuario=?";

        try (Connection conn = GerenciadorConexao.obterConexao();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, usuario.getNome());
            pstm.setString(2, usuario.getUsername());
            pstm.setString(3, usuario.getPassword());
            pstm.setString(4, usuario.getTelefone());
            pstm.setString(5, usuario.getRua());
            pstm.setString(6, usuario.getCidade());
            pstm.setString(7, usuario.getEstado());
            pstm.setInt(8, usuario.getId());

            pstm.executeUpdate();
        }
    }

    @Override
    public Usuario read(Integer k) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE id_usuario = ?";
        try (Connection conn = GerenciadorConexao.obterConexao();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setInt(1, k);
            ResultSet result = pstm.executeQuery();

            Usuario u = null;

            if (result.next()) {
                u = new Usuario();
                u.setId(result.getInt("id_usuario"));
                u.setNome(result.getString("nome"));
                u.setUsername(result.getString("username"));
                u.setPassword(result.getString("senha"));
                u.setTelefoneNum(result.getString("telefone"));
                u.setRua(result.getString("rua"));
                u.setCidade(result.getString("cidade"));
                u.setEstado(result.getString("estado"));
            }
            return u;
        }
    }

    @Override
    public void delete(Integer id_usuario) throws SQLException {
        String sql = "DELETE FROM Usuario WHERE id_usuario = ?";

        try (Connection conn = GerenciadorConexao.obterConexao();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, id_usuario);
            pstm.executeUpdate();
        }
    }

    public Usuario findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Usuario WHERE username = ?";

        try (Connection conn = GerenciadorConexao.obterConexao();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, username);
            ResultSet result = pstm.executeQuery();

            if (result.next()) {
                Usuario u = new Usuario();
                u.setId(result.getInt("id_usuario"));
                u.setNome(result.getString("nome"));
                u.setUsername(result.getString("username"));
                u.setPassword(result.getString("senha"));
                u.setTelefoneNum(result.getString("telefone"));
                u.setRua(result.getString("rua"));
                u.setCidade(result.getString("cidade"));
                u.setEstado(result.getString("estado"));

                return u;
            }
        }
        return null;
    }


    public void save(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (username, senha) VALUES (?, ?)";

        try (Connection conn = GerenciadorConexao.obterConexao();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setString(1, usuario.getUsername());
            pstm.setString(2, usuario.getPassword());
            pstm.executeUpdate();
        }
    }
} 
