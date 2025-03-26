package com.devcaotics.agenda.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.devcaotics.agenda.model.Contato;
import com.devcaotics.agenda.model.Telefone;
import com.devcaotics.agenda.model.Usuario;
import com.devcaotics.agenda.repositories.ContatoRepository;
import com.devcaotics.agenda.repositories.ComponenteGerenciador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contato")
public class ContatoController {

    private String msg = null;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private HttpSession session;
    @Autowired
    private ComponenteGerenciador componenteGerenciador;
    @Autowired
    private ContatoRepository contatoRepository;

    @GetMapping({"", "/", "listar"})
    public String listarContatos(Model m, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            redirectAttributes.addFlashAttribute("msg", "O usuário não está logado");
            return "redirect:/login";
        }

        try {
            List<Contato> contatos = contatoRepository.readAll(usuarioLogado.getId());
            m.addAttribute("contatos", contatos);
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("msg", "Erro ao buscar lista de contatos");
            return "redirect:/index";
        }
        return "index";
    }

    @PostMapping("/criar")
    public String criarContato(Contato contato, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        try {
            contatoRepository.create(contato, usuarioLogado.getId());
            return "redirect:/index";
        } catch (SQLException e) {
            e.printStackTrace();
            return "redirect:/erro";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarContatoTela(Model m, @PathVariable("id") Integer id) {
        try {
            Contato c = componenteGerenciador.read(id);

            if (c == null) {
                this.msg = "Contato não existente";
                return "redirect:/index";
            }

            if (c.getTelefones() == null) {
                c.setTelefones(new ArrayList<>()); 
            }

            m.addAttribute("contato", c);
            return "editar_contato";
        } catch (SQLException e) {
            this.msg = "Não foi possível editar contato";
            return "redirect:/index";
        }
    }

    @PostMapping("/editar")
    public String editarContato(@ModelAttribute Contato contato, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        try {
            for (Telefone telefone : contato.getTelefones()) {
                if (telefone.getId() == null) {
                    throw new SQLException("ID do telefone não pode ser vazio");
                }
            }

            contato.setUsuario(usuarioLogado);
            contatoRepository.update(contato, usuarioLogado.getId());
            redirectAttributes.addFlashAttribute("msg", "Contato editado com sucesso");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("msg", "Erro ao editar contato: " + e.getMessage());
        }
        return "redirect:/contato/listar";
    }

    @SuppressWarnings("finally")
    @GetMapping("/deletar/{id}")
    public String deletarContato(Model m, @PathVariable Integer id) {
        try {
            componenteGerenciador.delete(id);
            this.msg = "Contato deletado";
        } catch (SQLException e) {
            this.msg = "Erro ao deletar contato";
        } finally {
            return "redirect:/index";
        }
    }

    @GetMapping("/pesquisar")
    public String pesquisarContatoPorNome(@RequestParam(name = "nome", required = false) String nome, Model model, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        try {
            List<Contato> contatos;
            if (nome != null && !nome.isEmpty()) {
                contatos = contatoRepository.findByNomeUsuario(nome, usuarioLogado.getId());
            } else {
                contatos = contatoRepository.findByUsuarioId(usuarioLogado.getId());
            }
            model.addAttribute("contatos", contatos);
        } catch (SQLException e) {
            model.addAttribute("msg", "Erro ao pesquisar contato");
        }
        return "index";
    }
}

