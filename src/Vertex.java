import java.util.*;

public class  Vertex {

    private int x, y;
    private String name;
    private List<Vertex> neighbors;


    public Vertex ( String name, int x, int y){
        this.name = name;
        this.x = x;
        this.y = y;
        this.neighbors = new ArrayList<>();
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getName() { return name; }
    public List<Vertex> getNeighbors() { return neighbors; }


    public void addNeighbor(Vertex neighbor) {
        neighbors.add(neighbor);
    }

    public void removeNeighbor(Vertex neighbor) {
        neighbors.remove(neighbor);
    }
    
}



