import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

public class Main {
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String RESET = "\u001B[0m";

    public static void main(String[] args) {
        try {
            // 1. Проверка аргументов
            if (args.length == 0) {
                System.out.println(YELLOW + "ℹ Использование: java Main <путь-к-json-файлу>" + RESET);
                System.out.println(YELLOW + "Пример: java Main data/graph.json" + RESET);
                return;
            }

            // 2. Загрузка графа
            Path jsonPath = Paths.get(args[0]);
            System.out.println(CYAN + "🔍 Загрузка графа: " + jsonPath.getFileName() + RESET);
            
            Graph graph = GraphParser.parseFromFile(jsonPath);
            System.out.println(GREEN + "✅ Граф успешно загружен!" + RESET);

            // 3. Вывод информации о графе
            printGraphDetails(graph);
            
            // 4. Топологическая сортировка
            processTopologicalSorting(graph);
            
        } catch (Exception e) {
            System.err.println(RED + "\n⛔ Ошибка: " + e.getMessage() + RESET);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printGraphDetails(Graph graph) {
        System.out.println("\n📊 Детали графа:");
        System.out.println("├─ Всего вершин: " + graph.getVertices().size());
        System.out.println("├─ Всего рёбер: " + countEdges(graph));
        System.out.println("└─ Список вершин:");
        
        for (Vertex v : graph.getVertices()) {
            System.out.printf("   ├─ %s%s%s [%d, %d] → Соседи: %s%n", 
                    YELLOW, v.getName(), RESET, 
                    v.getX(), v.getY(),
                    formatNeighbors(v));
        }
    }
    
    private static int countEdges(Graph graph) {
        int count = 0;
        for (Vertex v : graph.getVertices()) {
            count += v.getNeighbors().size();
        }
        return count;
    }
    
    private static String formatNeighbors(Vertex vertex) {
        StringBuilder sb = new StringBuilder();
        for (Vertex neighbor : vertex.getNeighbors()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(neighbor.getName());
        }
        return sb.length() > 0 ? sb.toString() : "нет";
    }

    private static void processTopologicalSorting(Graph graph) {
        TopologicalSorter sorter = new TopologicalSorter(graph);
        
        System.out.println("\n" + BLUE + "🔄 Топологическая сортировка" + RESET);
        System.out.println(GREEN + "→ Шаги вперед:" + RESET);
        
        // Шаги вперед
        int step = 1;
        while (sorter.hasNext()) {
            Vertex current = sorter.next();
            List<Vertex> sorted = sorter.getSortedSoFar();
            System.out.printf("%d. Обработана: %s%s%s | Текущий порядок: %s%n", 
                    step++, 
                    GREEN, current.getName(), RESET,
                    formatOrder(sorted));
        }
        
        // Шаги назад
        System.out.println(RED + "\n← Шаги назад:" + RESET);
        try {
            int backStep = 1;
            while (!sorter.getSortedSoFar().isEmpty()) {
                Vertex prev = sorter.prev();
                List<Vertex> sorted = sorter.getSortedSoFar();
                System.out.printf("%d. Возвращена: %s%s%s | Текущий порядок: %s%n", 
                        backStep++,
                        RED, prev.getName(), RESET,
                        formatOrder(sorted));
            }
            System.out.println(GREEN + "✓ Достигнуто начальное состояние" + RESET);
        } catch (NoSuchElementException e) {
            System.out.println(RED + "✗ Ошибка отката: " + e.getMessage() + RESET);
        }
        
        System.out.println(GREEN + "\n✅ Процесс завершен успешно!" + RESET);
    }
    
    private static String formatOrder(List<Vertex> vertices) {
        StringBuilder sb = new StringBuilder();
        for (Vertex v : vertices) {
            if (sb.length() > 0) sb.append(" → ");
            sb.append(v.getName());
        }
        return sb.length() > 0 ? sb.toString() : "пусто";
    }
}