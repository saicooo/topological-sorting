import java.util.*;

/**
 * Представляет вершину графа с координатами и списком смежных вершин
 */
public class  Vertex {
    private final int x, y;
    private final String name;
    private final int _hash_code;
    private List<Vertex> neighbors;

    /**
     * Создает вершину только с именем
     * Координаты устанавливаются в (0,0)
     * 
     * @param name имя вершины
     */

    public Vertex(String name) {
        this(name, 0, 0);
    }

    /**
     * Создает вершину с указанным именем и координатами
     * 
     * @param name имя вершины
     * @param x координата X
     * @param y координата Y
     */
    public Vertex (String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this._hash_code = _calc_hash_code();
        this.neighbors = new ArrayList<>();
    }

    /**
     * @return координата X вершины
     */
    public int getX() { return x; }

    /**
     * @return координата Y вершины
     */
    public int getY() { return y; }

    /**
     * @return имя вершины
     */
    public String getName() { return name; }

    /**
     * Возвращает список соседних вершин, в которые есть исходящие рёбра.
     * Список недоступен для изменения извне.
     * 
     * @return неизменяемый список соседей
     */    
    public List<Vertex> getNeighbors() {
        return Collections.unmodifiableList(neighbors);
    }

    /**
     * Добавляет смежную вершину (рёберное соединение).
     * 
     * @param neighbor вершина-сосед
     */
    public void addNeighbor(Vertex neighbor) {
        if (neighbors.contains(neighbor)) {
            throw new IllegalArgumentException(
                "Сосед уже существует: " + neighbor.getName() + " для вершины " + this.name
            );
        }
        neighbors.add(neighbor);
    } 

    /**
     * Удаляет указанного соседа из списка смежных вершин.
     * 
     * @param neighbor вершина-сосед, которую нужно удалить
     */
    public void removeNeighbor(Vertex neighbor) {
        neighbors.remove(neighbor);
    }

    /**
     * Проверяет равенство вершин по предрассчитанному хеш-коду.
     * 
     * @param obj объект для сравнения
     * @return true, если вершины считаются равными
     */
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

    /**
     * @return хеш-код вершины
     */
    @Override 
    public int hashCode() {
        return _hash_code;
    }

    /**
     * Вычисляет хеш-код для вершины на основе имени и координат.
     * 
     * @return целочисленный хеш-код
     */
    private int _calc_hash_code() {
        int hash = name != null ? name.hashCode() : 0;
        hash = (hash << 16) | (hash >>> 16);
        hash ^= (x << 16) | (x >>> 16);
        hash ^= y;
        return hash;
    }
    
    /**
     * @return строковое представление вершины и её соседей
     */
    @Override
    public String toString() {
        return String.format("Vertex(%s (%d, %d) -> %s)", name, x, y,
            neighbors.stream().map(Vertex::getName).toList());
    }

}

