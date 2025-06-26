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

    public void addNeighbor(Vertex neighbors) {
        neighbors.add(neighbor);
    }


}



