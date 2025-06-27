import java.util.*;

public class TopologicalSorter {

    public static List<Vertex> sort(Graph graph) {

        // накапливает итоговый
        List<Vertex> result = new ArrayList<>();
        
        // карта входных степеней
        Map<Vertex, Integer> inDegree = new HashMap<>();

        // inDegree[v] = 0 
        for (Vertex v : graph.getVertices()) {
            inDegree.put(v, 0);
        }

        // v->u: inDegree[u]++
        for (Vertex v : graph.getVertices()) {
            for (Vertex u : v.getNeighbors()) {
                inDegree.put(u, inDegree.get(u) + 1);
            }
        }

        Deque<Vertex> queue = new ArrayDeque<>();

        // все вершины с inDegree = 0, в дек
        for (Map.Entry<Vertex, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.addLast(entry.getKey());
            }
        }

        
        while (!queue.isEmpty()) {
            Vertex v = queue.removeFirst();
            result.add(v);

            // для каждого соседа удаляем ребро v->neigh
            for (Vertex neigh : v.getNeighbors()) {
                int deg = inDegree.get(neigh) - 1;
                inDegree.put(neigh, deg);

                if (deg == 0){
                    queue.addLast(neigh);
                }
            }
        }

        return result;

    }

    
}