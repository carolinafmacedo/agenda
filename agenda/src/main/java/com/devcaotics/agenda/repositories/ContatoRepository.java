package com.devcaotics.agenda.repositories;
import com.devcaotics.agenda.model.Contato;
import com.devcaotics.agenda.model.Telefone;
import com.devcaotics.agenda.model.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public final class ContatoRepository implements RepositorioGenerico<Contato, Integer> {

    public ContatoRepository() {}

    @Override
    public void create(Contato contato) throws SQLException {
        throw new UnsupportedOperationException("Método não implementado");
    }

    public void create(Contato contato, Integer id_usuario) throws SQLException {
        String sqlContato = "INSERT INTO Contato (nome, email, id_usuario, rua, cidade, estado) VALUES(?,?,?,?,?,?)";

        try (Connection conn = GerenciadorConexao.obterConexao()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstm = conn.prepareStatement(sqlContato, Statement.RETURN_GENERATED_KEYS)) {
                pstm.setString(1, contato.getNome());
                pstm.setString(2, contato.getEmail());
                pstm.setInt(3, id_usuario);
                pstm.setString(4, contato.getRua());
                pstm.setString(5, contato.getCidade());
                pstm.setString(6, contato.getEstado());
                pstm.executeUpdate();

                try (ResultSet generatedKeys = pstm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        contato.setId(generatedKeys.getInt(1));
                    }
                }
            }

            if (contato.getTelefones() != null && !contato.getTelefones().isEmpty()) {
                String sqlTelefone = "INSERT INTO Telefone(numero, id_contato) VALUES(?,?)";
                try (PreparedStatement pstmTelefone = conn.prepareStatement(sqlTelefone)) {
                    for (Telefone telefone : contato.getTelefones()) {
                        pstmTelefone.setString(1, telefone.getNumero());
                        pstmTelefone.setInt(2, contato.getId());
                        pstmTelefone.addBatch();
                    }
                    pstmTelefone.executeBatch();
                }
            }
            conn.commit();
        }
    }

    @Override
    public void update(Contato contato) throws SQLException {
        throw new UnsupportedOperationException("Método não implementado");
    }

    public void update(Contato contato, Integer id_usuario) throws SQLException {
        String sqlContato = "UPDATE Contato SET nome=?, email=?, id_usuario=?, rua=?, cidade=?, estado=? WHERE id=?";

        try (Connection conn = GerenciadorConexao.obterConexao()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmContato = conn.prepareStatement(sqlContato)) {
                pstmContato.setString(1, contato.getNome());
                pstmContato.setString(2, contato.getEmail());
                pstmContato.setInt(3, id_usuario);
                pstmContato.setString(4, contato.getRua());
                pstmContato.setString(5, contato.getCidade());
                pstmContato.setString(6, contato.getEstado());
                pstmContato.setInt(7, contato.getId());
                pstmContato.executeUpdate();
            }

            if (contato.getTelefones() != null && !contato.getTelefones().isEmpty()) {
                String sqlTelefone = "UPDATE Telefone SET numero=? WHERE id=? AND id_contato=?";
                try (PreparedStatement pstmTelefone = conn.prepareStatement(sqlTelefone)) {
                    for (Telefone telefone : contato.getTelefones()) {
                        if (telefone.getId() == null) {
                            throw new SQLException("ID do telefone não pode ser nulo");
                        }
                        pstmTelefone.setString(1, telefone.getNumero());
                        pstmTelefone.setInt(2, telefone.getId());
                        pstmTelefone.setInt(3, contato.getId());
                        pstmTelefone.addBatch();
                    }
                    pstmTelefone.executeBatch();
                }
            }

            conn.commit();
        }
    }

    @Override
    public Contato read(Integer id) throws SQLException {
        String sql = "SELECT * FROM Contato WHERE id = ?";

        try (Connection conn = GerenciadorConexao.obterConexao();
             PreparedStatement pstm = conn.prepareStatement(sql)) {

            pstm.setInt(1, id);
            ResultSet result = pstm.executeQuery();

            if (result.next()) {
                Contato contato = new Contato();
                contato.setId(result.getInt("id"));
                contato.setNome(result.getString("nome"));
                contato.setEmail(result.getString("email"));
                contato.setRua(result.getString("rua"));
                contato.setCidade(result.getString("cidade"));
                contato.setEstado(result.getString("estado"));

                Usuario usuario = new Usuario();
                usuario.setId(result.getInt("id_usuario"));
                contato.setUsuario(usuario);

                contato.setTelefones(getTelefonesPorContato(id, conn));

                return contato;
            }
            return null;
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        String sql = "DELETE FROM Contato WHERE id = ?";

        try (Connection conn = GerenciadorConexao.obterConexao();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, id);
            pstm.executeUpdate();
        }
    }

    public List<Contato> readAll(Integer id_usuario) throws SQLException {
        String sql = "SELECT * FROM Contato WHERE id_usuario = ?";
        List<Contato> contatos = new ArrayList<>();

        try (Connection conn = GerenciadorConexao.obterConexao();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, id_usuario);
            ResultSet result = pstm.executeQuery();

            while (result.next()) {
                Contato contato = new Contato();
                contato.setId(result.getInt("id"));
                contato.setNome(result.getString("nome"));
                contato.setEmail(result.getString("email"));
                contato.setRua(result.getString("rua"));
                contato.setCidade(result.getString("cidade"));
                contato.setEstado(result.getString("estado"));

                Usuario usuario = new Usuario();
                usuario.setId(result.getInt("id_usuario"));
                contato.setUsuario(usuario);

                contato.setTelefones(getTelefonesPorContato(contato.getId(), conn));
                contatos.add(contato);
            }
        }
        return contatos;
    }

    public List<Contato> findByUsuarioId(Integer id_usuario) throws SQLException {
        return readAll(id_usuario);
    }

    public List<Contato> findByNomeUsuario(String nome, Integer id_usuario) throws SQLException {
        String sql = "SELECT * FROM Contato WHERE nome LIKE ? AND id_usuario = ?";
        List<Contato> contatos = new ArrayList<>();

        try (Connection conn = GerenciadorConexao.obterConexao();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, "%" + nome + "%");
            pstm.setInt(2, id_usuario);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Contato contato = new Contato();
                contato.setId(rs.getInt("id"));
                contato.setNome(rs.getString("nome"));
                contato.setEmail(rs.getString("email"));
                contato.setRua(rs.getString("rua"));
                contato.setCidade(rs.getString("cidade"));
                contato.setEstado(rs.getString("estado"));

                contatos.add(contato);
            }
        }
        return contatos;
    }

    private List<Telefone> getTelefonesPorContato(Integer contatoId, Connection conn) throws SQLException {
        List<Telefone> telefones = new ArrayList<>();
        String sqlTelefone = "SELECT * FROM Telefone WHERE id_contato = ?";

        try (PreparedStatement pstmTelefone = conn.prepareStatement(sqlTelefone)) {
            pstmTelefone.setInt(1, contatoId);
            ResultSet result = pstmTelefone.executeQuery();

            while (result.next()) {
                Telefone telefone = new Telefone();
                telefone.setId(result.getInt("id"));
                telefone.setNumero(result.getString("numero"));
                telefones.add(telefone);
            }
        }
        return telefones;
    }
}

