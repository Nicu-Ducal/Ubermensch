package database;

import java.util.List;

/**
 * Interface for CRUD operations
 * @param <T> - The class for which the CRUD is implemented
 */
public interface CRUD<T> {
    int create(T obj);
    List<T> readAll();
    T read(int id);
    void update(int id, T newObj);
    void delete(int id);
}
