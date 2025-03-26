package com.devcaotics.agenda.repositories;

import java.sql.SQLException;

public interface Repository<T, K> {

    void create(T entity) throws SQLException;

    void update(T entity) throws SQLException;

    T read(K key) throws SQLException;

    void delete(K key) throws SQLException;
}


