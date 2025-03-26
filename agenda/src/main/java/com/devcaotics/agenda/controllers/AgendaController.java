package com.devcaotics.agenda.controllers;

import java.sql.SQLException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.devcaotics.agenda.model.Usuario;
import com.devcaotics.agenda.repositories.GerenciadorUsuarios;
import com.devcaotics.agenda.repositories.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;

@Controller
public class AgendaController {

    private final UsuarioRepository usuarioRepository;


    @Autowired
    public AgendaController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;

    }

    @RequestMapping({"/", "", "/*"})
    public String menuPrincipal(HttpSession session) {
        if(session.getAttribute("usuarioLogado") != null){
            return "redirect:/home";
        }else{
            return "redirect:/login";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String autenticarUsuario(HttpSession session, @RequestParam String username, @RequestParam String senha, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioRepository.findByUsername(username);
            if (usuario != null && BCrypt.checkpw(senha, usuario.getSenha())) {
                session.setAttribute("usuarioLogado", usuario);
                return "redirect:/home";
            } else {
                return "redirect:/login?error=true";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("msg", "Erro na autenticação do usuário");
            return "redirect:/login?error=true";
        }
    }

    @GetMapping("/cadastro")
    public String paginaCadastro() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            if (usuarioRepository.findByUsername(usuario.getUsername()) != null) {
                redirectAttributes.addFlashAttribute("msg", "Usuário já existente");
                return "redirect:/cadastro";
            }
            usuarioRepository.save(usuario);
            redirectAttributes.addFlashAttribute("msg", "Cadastro realizado com sucesso");
            return "redirect:/login";
        } catch (SQLException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("msg", "Erro ao realizar cadastro do usuário");
            return "redirect:/cadastro";
        }
    }

    @GetMapping("/home")
    public String home(Model m, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            redirectAttributes.addFlashAttribute("msg", "Usuário não está logado");
            return "redirect:/login";
        }
        m.addAttribute("nomeUsuario", usuarioLogado.getNome());
        m.addAttribute("usuarioLogado", usuarioLogado);

        try {
            m.addAttribute("contatos", GerenciadorUsuarios.getCurrentInstance().readAll(usuarioLogado.getId()));
        } catch (SQLException e) {
            m.addAttribute("msg", "Erro ao carregar a agenda");
        }
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    private boolean isValidUser(String username, String senha) {
        try {
            Usuario user = usuarioRepository.findByUsername(username);
            return user != null && user.getSenha().equals(senha);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

