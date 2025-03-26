package com.devcaotics.agenda.repositories;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.devcaotics.agenda.model.Contato;
import org.springframework.stereotype.Component;

@Component
public class ComponenteGerenciador {

    private RepositorioGenerico<Contato, Integer> rContato = null;

    public ComponenteGerenciador() {
        rContato = new ContatoRepository();
    }

    public void create(Contato c) throws SQLException {
        this.rContato.create(c);
    }

    public void update(Contato c) throws SQLException {
        this.rContato.update(c);
    }

    public Contato read(int id) throws SQLException {
        return this.rContato.read(id);
    }

    public void delete(int id) throws SQLException {
        this.rContato.delete(id);
    }

    public List<Contato> readAll(Integer id_usuario) throws SQLException {
        List<Contato> contatos = ((ContatoRepository) this.rContato).readAll(id_usuario);
        return (contatos != null) ? contatos : new ArrayList<>();
    }
}
