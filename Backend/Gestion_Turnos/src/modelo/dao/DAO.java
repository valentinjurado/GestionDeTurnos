package modelo.dao;
import java.util.List;
import java.awt.*;

public interface DAO<T> {
    void insertar(T objeto);
    List<T> listarTodos();





}
