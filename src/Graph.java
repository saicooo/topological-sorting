import java.util.*;

public class Graph {
    private List<Vertex> vertices;
    private List<Edge> edges;

    public Graph() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    // добавление вершин
    public void addVertex (Vertex vertex) {
        vertices.add(vertex);
    }

    // добавление ребра
    public void addEdge (Edge edge) {
        edges.add(edge);
    }

    //получаем вершину по имени
    public Vertex getVertexByName (String name) {
        for (Vertex v : vertices) {
            if (v.name.equals(name)) return v;
        }
        return null;
    }

    // получаем индекс вершины в списке вершин по имени
    public int getVertexIndexByName (String name) {
        for (int i = 0; i != vertices.size(); i++) {
            if (vertices.get(i).name.equals(name)) {
                return i;
            }
        }
        return -1; // если вершины нет с именем (че-нить бы придумать наверное)
    }

    // список вершин
    public List<Vertex> getVertices() {
        return vertices;
    }

    // список ребер
    public List<Edge> getEdges() {
        return edges;
    }

    // соседи (куда направлены стрелки)
    public List<Integer> getNeighbors(int vertexIndex) {
        List<Integer> neighbors = new ArrayList<>();
        for (Edge e : edges) {
            if (e.fromVertex == vertexIndex) {
                neighbors.add(e.toVertex);
            }
        }
        return neighbors;
    }

    // вывод
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Edge e : edges) {
            String from = vertices.get(e.fromVertex).name;
            String to = vertices.get(e.toVertex).name;
            sb.append(from).append(" --> ").append(to).append("\n");
        }
        return sb.toString();
    }


}
