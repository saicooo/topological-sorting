import java.util.*;

public class Graph {
    private List<Vertex> vertices;

    public Graph() {
        this(new ArrayList<>());
    }

    public Graph(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    // добавление вершин
    public void addVertex (Vertex vertex) {
        if (exists(vertex)) {
            throw new IllegalArgumentException("Вершина в графе уже существует");
        }

        vertices.add(vertex);
    }

    // добавление ребра
    public void addEdge(Vertex _s, Vertex _e) {
        if (!exists(_s) || !exists(_e)) {
            throw new IllegalArgumentException("Вершины добавляемых рёбер должны существовать в графе");
        }
        
        _s.addNeighbor(_e);
    }

    // получаем вершину по имени
    public Vertex getVertexByName (String name) {
        for (Vertex v : vertices) {
            if (v.getName().equals(name)) {
                return v;
            }
        }
        return null;
    }

    // список вершин
    public List<Vertex> getVertices() {
        return vertices;
    }

    // проверка на существование вершины в графе
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

