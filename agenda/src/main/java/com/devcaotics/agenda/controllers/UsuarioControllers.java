package com.devcaotics.agenda.controllers;

import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.devcaotics.agenda.repositories.GerenciadorUsuarios;
import com.devcaotics.agenda.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuarioControllers {

    private String msg = null;
    private GerenciadorUsuarios gerenciadorUsuarios;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private HttpSession session;

    public UsuarioControllers() {
        this.gerenciadorUsuarios = GerenciadorUsuarios.getCurrentInstance();
    }

    @GetMapping({"", "/", "listar"})
    public String listarUsuario(Model m, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
            if (usuarioLogado == null) {
                redirectAttributes.addFlashAttribute("msg", "Nenhum usuário se encontra logado");
                return "redirect:/login";
            }
            m.addAttribute("usuario", usuarioLogado);
            if (this.msg != null) {
                m.addAttribute("msg", this.msg);
                this.msg = null;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "Erro ao pesquisar o usuário logado");
            return "redirect:/";
        }
        return "usuario/listar";
    }

    @PostMapping("/criar")
    public String criarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            String senhaCriptografada = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
            usuario.setSenha(senhaCriptografada);
            gerenciadorUsuarios.create(usuario);
            session.setAttribute("usuarioLogado", usuario);
            redirectAttributes.addFlashAttribute("msg", "Cadastramento do usuário foi feito com sucesso!");
            return "redirect:/home";
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("msg", "Erro no cadastramento do usuário");
            return "redirect:/login";
        }
    }

    @GetMapping("/editar/{id}")
    public String telaEditarUsuario(Model m, @PathVariable("id") Integer id_usuario, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = gerenciadorUsuarios.readUsuario(id_usuario);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("msg", "Usuário não encontrado");
                return "redirect:/home";
            }
            m.addAttribute("usuario", usuario);
            return "editar_usuario";
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("msg", "Erro no carregamento dos dados do usuário");
            return "redirect:/home";
        }
    }

    @PostMapping("/editar")
    public String editarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuarioAtual = gerenciadorUsuarios.readUsuario(usuario.getId());
            if (usuarioAtual == null) {
                redirectAttributes.addFlashAttribute("msg", "Usuário não encontrado");
                return "redirect:/home";
            }
            if (usuario.getNome() != null && !usuario.getNome().isEmpty()) {
                usuarioAtual.setNome(usuario.getNome());
            }
            if (usuario.getUsername() != null && !usuario.getUsername().isEmpty()) {
                usuarioAtual.setUsername(usuario.getUsername());
            }
            if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
                usuarioAtual.setSenha(BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt()));
            }
            if (usuario.getRua() != null && !usuario.getRua().isEmpty()) {
                usuarioAtual.setRua(usuario.getRua());
            }
            if (usuario.getCidade() != null && !usuario.getCidade().isEmpty()) {
                usuarioAtual.setCidade(usuario.getCidade());
            }
            if (usuario.getEstado() != null && !usuario.getEstado().isEmpty()) {
                usuarioAtual.setEstado(usuario.getEstado());
            }
            gerenciadorUsuarios.update(usuarioAtual);
            redirectAttributes.addFlashAttribute("msg", "Informações do usuário foram atualizadas com sucesso");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("msg", "Erro ao atualizar informações do usuário");
        }
        return "redirect:/home";
    }

    @GetMapping("/deletar/{id}")
    public String deletarUsuario(@PathVariable Integer id_usuario, RedirectAttributes redirectAttributes) {
        try {
            gerenciadorUsuarios.deleteUsuario(id_usuario);
            redirectAttributes.addFlashAttribute("msg", "Usuário deletado com sucesso");
        } catch (SQLException e) {
            redirectAttributes.addFlashAttribute("msg", "Erro ao deletar usuário");
        }
        return "redirect:/login";
    }
}

