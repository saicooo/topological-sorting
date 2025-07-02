import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class GraphParser {

    // Приватный конструктор - утилитарный класс
    private GraphParser() {}

    /**
     * Парсит граф из JSON-файла
     * 
     * @param filePath путь к JSON-файлу
     * @return объект Graph
     * @throws Exception при ошибках чтения/парсинга
     */
    public static Graph parseFromFile(Path filePath) throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            return parse(reader);
        }
    }

    /**
     * Парсит граф из Reader
     * 
     * @param reader источник JSON-данных
     * @return объект Graph
     * @throws JsonSyntaxException при ошибке синтаксиса JSON
     * @throws IllegalArgumentException при невалидных данных
     */
    public static Graph parse(Reader reader) {
        Gson gson = new Gson();
        GraphJsonData graphData = gson.fromJson(reader, GraphJsonData.class);

        // Валидация структуры JSON
        validateGraphData(graphData);

        // Создаем вершины
        List<Vertex> vertices = createVertices(graphData.vertices);
        Graph graph = new Graph(vertices);

        // Добавляем ребра
        addEdges(graph, vertices, graphData.edges);

        return graph;
    }

    private static void validateGraphData(GraphJsonData graphData) {
        if (graphData.vertices == null) {
            throw new IllegalArgumentException("Отсутствует массив vertices в JSON");
        }
        if (graphData.edges == null) {
            graphData.edges = new ArrayList<>(); // Разрешаем пустые графы
        }
    }

    private static List<Vertex> createVertices(List<VertexJsonData> verticesData) {
        List<Vertex> vertices = new ArrayList<>(verticesData.size());
        for (int i = 0; i < verticesData.size(); i++) {
            VertexJsonData vData = verticesData.get(i);
            validateVertexData(vData, i);
            vertices.add(new Vertex(vData.name, vData.x, vData.y));
        }
        return vertices;
    }

    private static void validateVertexData(VertexJsonData vData, int index) {
        if (vData.name == null || vData.name.isEmpty()) {
            throw new IllegalArgumentException(
                "Вершина #" + index + " имеет пустое имя"
            );
        }
    }

    private static void addEdges(Graph graph, List<Vertex> vertices, List<EdgeJsonData> edgesData) {
        for (int i = 0; i < edgesData.size(); i++) {
            EdgeJsonData eData = edgesData.get(i);
            validateEdgeData(eData, i, vertices.size());
            
            Vertex source = vertices.get(eData.vertex1);
            Vertex target = vertices.get(eData.vertex2);
            graph.addEdge(source, target);
        }
    }

    private static void validateEdgeData(EdgeJsonData eData, int index, int vertexCount) {
        if (eData.controlStep != 0) {
            throw new IllegalArgumentException(
                "Поле controlStep должно быть 0 в ребре #" + index
            );
        }
        
        if (!eData.isDirected) {
            throw new IllegalArgumentException(
                "Топологическая сортировка требует ориентированный граф (ребро #" + index + ")"
            );
        }
        validateVertexIndex(eData.vertex1, index, vertexCount, "vertex1");
        validateVertexIndex(eData.vertex2, index, vertexCount, "vertex2");
    }

    private static void validateVertexIndex(int index, int edgeIndex, int max, String field) {
        if (index < 0 || index >= max) {
            throw new IllegalArgumentException(
                String.format("Неверный индекс вершины %s: %d (допустимо 0-%d) в ребре #%d",
                field, index, max - 1, edgeIndex)
            );
        }
    }

    // Внутренние классы для структуры JSON
    private static class GraphJsonData {
        List<VertexJsonData> vertices;
        List<EdgeJsonData> edges;
    }

    private static class VertexJsonData {
        int x;
        int y;
        String name;
    }

    private static class EdgeJsonData {
        int vertex1;
        int vertex2;
        boolean isDirected;
        int controlStep;
    }
}