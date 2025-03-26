package com.devcaotics.agenda.repositories;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import com.devcaotics.agenda.model.Contato;
import com.devcaotics.agenda.model.Usuario;

public class GerenciadorUsuarios {

    private static GerenciadorUsuarios myself = null;

    private RepositorioGenerico<Contato, Integer> rContato = null;
    private RepositorioGenerico<Usuario, Integer> rUsuario = null;

    private GerenciadorUsuarios() {
        this.rContato = new ContatoRepository();
        this.rUsuario = new UsuarioRepository();
    }

    public static GerenciadorUsuarios getCurrentInstance() {
        if (myself == null) {
            myself = new GerenciadorUsuarios();
        }
        return myself;
    }

    public void create(Contato c) throws SQLException {
        this.rContato.create(c);
    }

    public void update(Contato c) throws SQLException {
        this.rContato.update(c);
    }

    public Contato readContato(int id) throws SQLException {
        return this.rContato.read(id);
    }

    public void deleteContato(int id) throws SQLException {
        this.rContato.delete(id);
    }

    public List<Contato> readAll(Integer id_usuario) throws SQLException {
        List<Contato> contatos = ((ContatoRepository) this.rContato).readAll(id_usuario);
        return (contatos != null) ? contatos : new ArrayList<>();
    }

    public void create(Usuario u) throws SQLException {
        this.rUsuario.create(u);
    }

    public void update(Usuario u) throws SQLException {
        this.rUsuario.update(u);
    }

    public Usuario readUsuario(int id_usuario) throws SQLException {
        return this.rUsuario.read(id_usuario);
    }

    public void deleteUsuario(int id_usuario) throws SQLException {
        this.rUsuario.delete(id_usuario);
    }

	public Object readContato(Long id, Integer id2) {
		return null;
	}
}
