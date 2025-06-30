import java.util.*;

public class TopologicalSorter implements ExtendedIterator<Vertex> {

    private final Graph graph;
    private final Map<Vertex, Integer> inDegree;
    private final Deque<Vertex> queue;
    private final List<Vertex> result;

    public TopologicalSorter(Graph graph) {
        this.graph = graph;
        this.inDegree = new HashMap<>();
        this.queue = new ArrayDeque<>();
        this.result = new ArrayList<>();

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

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public Vertex next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements");
        }
        return iterateStep();
    }


    private Vertex iterateStep() {
        Vertex v = queue.removeFirst();
        result.add(v);

        for (Vertex neigh : v.getNeighbors()) {
            int deg = inDegree.get(neigh) - 1;
            inDegree.put(neigh, deg);
            if (deg == 0) {
                queue.addLast(neigh);
            }
        }
        return v;
    }

    @Override
    public Vertex prev() {
        if (result.isEmpty()) {
            throw new NoSuchElementException("No previous element to revert");
        }
        Vertex v = result.remove(result.size() - 1);
        for (Vertex neighbor : v.getNeighbors()) {
            if (inDegree.get(neighbor) == 0) {
                queue.remove(neighbor);
            }
            inDegree.put(neighbor, inDegree.get(neighbor) + 1);
        }
        queue.addFirst(v);
        return v;
    }


    public List<Vertex> getSortedSoFar() {
        return Collections.unmodifiableList(result);
    }

    
}