package org.shdevelopment.Database;

import java.util.Optional;

public interface EntityManagerInterface<T> {
    void createEntity(T t);
    T readEntity(long id);
    void updateEntity(T t);
    void deleteEntity(T t);
}