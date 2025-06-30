import java.util.*;

/**
 * Класс, выполняющий топологическую сортировку графа с возможностью
 * пошагового прохождения вперед и назад.
 */
public class TopologicalSorter implements ExtendedIterator<Vertex> {

    private final Graph graph;
    private final Map<Vertex, Integer> inDegree;
    private final Deque<Vertex> queue;
    private final List<Vertex> result;
    private final Deque<List<Vertex>> addedToQueueHistory;

    /**
     * Создает объект сортировщика для заданного графа.
     * При создании вычисляет степени входа всех вершин
     * и формирует очередь вершин с нулевой степенью.
     *
     * @param graph граф, который будет отсортирован
     */
    public TopologicalSorter(Graph graph) {
        this.graph = graph;
        this.inDegree = new HashMap<>();
        this.queue = new ArrayDeque<>();
        this.result = new ArrayList<>();
        this.addedToQueueHistory = new ArrayDeque<>();

        for (Vertex v : graph.getVertices()) {
            inDegree.put(v, 0);
        }

        for (Vertex v : graph.getVertices()) {
            for (Vertex u : v.getNeighbors()) {
                inDegree.put(u, inDegree.get(u) + 1);
            }
        }

        for (Map.Entry<Vertex, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.addLast(entry.getKey());
            }
        }

    }

    /**
     * Возвращает неизменяемый доступ к исходному графу.
     *
     * @return интерфейс графа
     */
    public IConstGraph getGraph() {
        return graph;
    }

    /**
     * Проверяет, остались ли ещё вершины, которые можно извлечь из очереди.
     *
     * @return true, если очередная вершина доступна
     */
    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    /**
     * Выполняет следующий шаг сортировки.
     * Удаляет вершину из очереди, уменьшает степени входа её соседей,
     * добавляет их в очередь, если их степень стала нулевой.
     *
     * @return вершина, обработанная на этом шаге
     * @throws NoSuchElementException если больше нет элементов
     */
    @Override
    public Vertex next() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements");
        }
        return iterateNextStep();
    }

    /**
     * Реализует шаг вперед в сортировке.
     * Обновляет очередь и историю.
     *
     * @return вершина, извлечённая на этом шаге
     */
    private Vertex iterateNextStep() {
        Vertex v = queue.removeFirst();
        result.add(v);

        List<Vertex> addedNow = new ArrayList<>();
        for (Vertex neigh : v.getNeighbors()) {
            int deg = inDegree.get(neigh) - 1;
            inDegree.put(neigh, deg);
            if (deg == 0) {
                queue.addLast(neigh);
                addedNow.add(neigh);
            }
        }
        addedToQueueHistory.push(addedNow);
        return v;
    }

    /**
     * Выполняет шаг назад.
     * Восстанавливает вершину обратно в очередь,
     * возвращает степень входа её соседей.
     *
     * @return вершина, возвращённая на шаг назад
     * @throws NoSuchElementException если нечего откатывать
     */
    @Override
    public Vertex prev() throws NoSuchElementException {
        if (result.isEmpty()) {
            throw new NoSuchElementException("No previous element to revert");
        }
        
        return iteratePrevStep();
    }

    /**
     * Реализует логику отката шага сортировки.
     * Обновляет очередь и историю.
     *
     * @return вершина, возвращённая на шаг назад
     */
    private Vertex iteratePrevStep() {
        Vertex v = result.remove(result.size() - 1);
        List<Vertex> addedNow = addedToQueueHistory.pop();
        for (Vertex neighbor : v.getNeighbors()) {
            inDegree.put(neighbor, inDegree.get(neighbor) + 1);
        }

        for (Vertex neighbor : addedNow) {
            queue.remove(neighbor);
        }

        queue.addFirst(v);
        return v;
    }

    /**
     * Возвращает неизменяемый список вершин,
     * которые уже были обработаны (отсортированы).
     *
     * @return список отсортированных вершин
     */
    public List<Vertex> getSortedSoFar() {
        return Collections.unmodifiableList(result);
    }
    
}

