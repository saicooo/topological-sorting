import java.util.*;

public class Graph implements IGraph {
    private List<Vertex> vertices;

    public Graph() {
        this(new ArrayList<>());
    }

    public Graph(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    // добавление вершин
    @Override
    public void addVertex (Vertex vertex) {
        if (exists(vertex)) {
            throw new IllegalArgumentException("Вершина в графе уже существует");
        }

        vertices.add(vertex);
    }

    // добавление ребра
    @Override
    public void addEdge(Vertex _s, Vertex _e) {
        if (!exists(_s) || !exists(_e)) {
            throw new IllegalArgumentException("Вершины добавляемых рёбер должны существовать в графе");
        }
        
        _s.addNeighbor(_e);
    }

    // получаем вершину по имени
    @Override
    public Vertex getVertexByName (String name) {
        for (Vertex v : vertices) {
            if (v.getName().equals(name)) {
                return v;
            }
        }
        return null;
    }

    // список вершин
    @Override
    public List<Vertex> getVertices() {
        return Collections.unmodifiableList(vertices);
    }

    // проверка на существование вершины в графе
    @Override
    public boolean exists(Vertex vertex) {
        for (Vertex v : vertices) {
            if (vertex.equals(v)) {
                return true;
            }
        }
        return false;
    }

    // вывод
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Graph:\n");
        for (Vertex v : vertices) {
            sb.append(v.toString()).append("\n");
        }
        return sb.toString();
    }

}

