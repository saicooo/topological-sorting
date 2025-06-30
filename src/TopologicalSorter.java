import java.util.*;

public class TopologicalSorter implements ExtendedIterator<Vertex> {

    private final Graph graph;
    private final Map<Vertex, Integer> inDegree;
    private final Deque<Vertex> queue;
    private final List<Vertex> result;
    private final Deque<List<Vertex>> addedToQueueHistory;

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

    public IConstGraph getGraph() {
        return graph;
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public Vertex next() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements");
        }
        return iterateStep();
    }


    private Vertex iterateStep() {
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

    @Override
    public Vertex prev() throws NoSuchElementException {
        if (result.isEmpty()) {
            throw new NoSuchElementException("No previous element to revert");
        }
        
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

    public List<Vertex> getSortedSoFar() {
        return Collections.unmodifiableList(result);
    }
    
}

