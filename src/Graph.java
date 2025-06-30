import java.util.*;

/**
 * Реализация направленного графа с возможностью добавления вершин и рёбер
 */
public class Graph implements IGraph {
    private List<Vertex> vertices;

    /**
     * Создает пустой граф
     */
    public Graph() {
        this(new ArrayList<>());
    }

    /**
     * Создает граф с заданным списком вершин
     *
     * @param vertices список вершин графа
     */
    public Graph(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    /**
     * Добавляет вершину в граф
     *
     * @param vertex вершина для добавления
     * @throws IllegalArgumentException если вершина уже существует в графе
     */
    @Override
    public void addVertex (Vertex vertex) {
        if (exists(vertex)) {
            throw new IllegalArgumentException("Вершина в графе уже существует");
        }

        vertices.add(vertex);
    }

    /**
     * Добавляет ориентированное ребро от вершины _s к вершине _e
     *
     * @param _s начальная вершина
     * @param _e конечная вершина
     * @throws IllegalArgumentException если хотя бы одна из вершин отсутствует в графе
     */
    @Override
    public void addEdge(Vertex _s, Vertex _e) {
        if (!exists(_s) || !exists(_e)) {
            throw new IllegalArgumentException("Вершины добавляемых рёбер должны существовать в графе");
        }
        
        _s.addNeighbor(_e);
    }

    /**
     * Возвращает вершину по имени
     *
     * @param name имя вершины
     * @return вершина с заданным именем или null, если такой вершины нет
     */
    @Override
    public Vertex getVertexByName (String name) {
        for (Vertex v : vertices) {
            if (v.getName().equals(name)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Возвращает список всех вершин графа
     *
     * @return немодифицируемый список вершин
     */
    @Override
    public List<Vertex> getVertices() {
        return Collections.unmodifiableList(vertices);
    }

    /**
     * Проверяет, существует ли вершина в графе
     *
     * @param vertex вершина для проверки
     * @return true, если вершина есть в графе, иначе false
     */
    @Override
    public boolean exists(Vertex vertex) {
        for (Vertex v : vertices) {
            if (vertex.equals(v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Возвращает строковое представление графа
     *
     * @return строка, описывающая граф
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Graph:\n");
        for (Vertex v : vertices) {
            sb.append(v.toString()).append("\n");
        }
        return sb.toString();
    }

}

