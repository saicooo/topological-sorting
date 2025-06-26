import java.util.*;

public class Graph {
    private List<Vertex> vertices;

    public Graph() {
        this.vertices = new ArrayList<>();
    }

    // добавление вершин
    public void addVertex (Vertex vertex) {
        vertices.add(vertex);
    }

    //получаем вершину по имени
    public Vertex getVertexByName (String name) {
        for (Vertex v : vertices) {
            if (v.name.equals(name)) {
                return v;
            }
        }
        return null;
    }

    // список вершин
    public List<Vertex> getVertices() {
        return vertices;
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
