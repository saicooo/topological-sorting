import java.util.*;

public class  Vertex {
    private final int x, y;
    private final String name;
    private final int _hash_code;
    private List<Vertex> neighbors;

    public Vertex(String name) {
        this(name, 0, 0);
    }

    public Vertex (String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this._hash_code = _calc_hash_code();
        this.neighbors = new ArrayList<>();
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getName() { return name; }
    public List<Vertex> getNeighbors() { return neighbors; }

    // добавление вершины, куда можно попасть из текущей
    public void addNeighbor(Vertex neighbor) {
        neighbors.add(neighbor);
    }

    // удаление вершины, куда можно было попасть из текущей
    public void removeNeighbor(Vertex neighbor) {
        neighbors.remove(neighbor);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Vertex vertex = (Vertex) obj;
        return _hash_code == vertex._hash_code;
    }

    @Override
    public int hashCode() {
        return _hash_code;
    }

    // одноразовое вычисление хеша для вершины
    private int _calc_hash_code() {
        int hash = name != null ? name.hashCode() : 0;
        hash = (hash << 16) | (hash >>> 16);
        hash ^= (x << 16) | (x >>> 16);
        hash ^= y;
        return hash;
    }
    
    @Override
    public String toString() {
        return String.format("Vertex(%s (%d, %d) -> %s)", name, x, y,
            neighbors.stream().map(Vertex::getName).toList());
    }

}

