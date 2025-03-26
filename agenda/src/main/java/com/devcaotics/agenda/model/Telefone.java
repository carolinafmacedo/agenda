package com.devcaotics.agenda.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Telefone {

    private Integer id;
    private String numero;
    private Contato id_contato;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Contato getContato() {
        return id_contato;
    }

    public void setContato(Contato id_contato) {
        this.id_contato = id_contato;
    }
}

