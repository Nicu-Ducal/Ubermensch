package database;

import java.util.List;

/**
 * Interface for transformation of .csv rows to object and vice-versa
 * @param <T> Class type
 */
public interface IDatabaseOperations<T> {
    List<T> getCollection();
    void load();
    T toObjectFromDB(String[] dbRow);
    String[] toDBString(T obj);
    T getElementById(Integer id);
}
