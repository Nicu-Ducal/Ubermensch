package database;

/**
 * Interface for transformation of .csv rows to object and vice-versa
 * @param <T> Class type
 */
public interface RWOperations<T> {
    T toObjectFromDB(String[] dbRow);
    String[] toDBString();
}
