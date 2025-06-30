import java.util.List;

public interface IConstGraph {
    Vertex getVertexByName (String name);
    List<Vertex> getVertices();
    boolean exists(Vertex vertex);
    
}

