package database;

/**
 * Interface for transformation of .csv rows to object and vice-versa
 * @param <T> Class type
 */
public interface IDatabaseOperations<T> {
    T toObjectFromDB(String[] dbRow, Object... services);
    String[] toDBString(T obj);
    T getElementById(Integer id);
}
