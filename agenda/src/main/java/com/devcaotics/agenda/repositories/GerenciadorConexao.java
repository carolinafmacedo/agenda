package com.devcaotics.agenda.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GerenciadorConexao {

    private static final String URL = "jdbc:postgresql://localhost:5432/agenda";
    private static final String USUARIO = "postgres";
    private static final String SENHA = "admin";

    static {
        registrarDriver();
    }

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static void fecharConexao(Connection conexao) {
        if (conexao != null) {
            try {
                conexao.close();
            } catch (SQLException e) {
                System.err.println("Erro ao realizar fechamento da conexão: " + e.getMessage());
            }
        }
    }

    public static boolean isConexaoValida(Connection conexao) {
        try {
            return conexao != null && !conexao.isClosed();
        } catch (SQLException e) {
            System.err.println("Erro ao realizar verificação de conexão: " + e.getMessage());
        }
        return false;
    }

    private static void registrarDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao carregar o driver JDBC: " + e.getMessage());
        }
    }
}
