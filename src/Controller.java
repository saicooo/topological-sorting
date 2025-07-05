import javafx.event.ActionEvent;
import javafx.fxml.FXML;
// import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
// import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Controller {

    @FXML private VBox mainContainer;
    @FXML private SplitPane mainSplitPane;
    @FXML private HBox graphContainer;
    @FXML private Pane graphCanvas;
    @FXML private HBox pathContainer;
    
    // Кнопки
    @FXML private Button BackButton;
    @FXML private Button ForwardButton;
    @FXML private Button OnLoadFromFile;
    @FXML private Button RunImmediately;
    
    @FXML private Label pathLabel;

    private Graph graph;
    private TopologicalSorter sorter;
    private final Map<Vertex, Circle> vertexNodes = new HashMap<>();
    private double scale = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;

    @FXML
    void onLoadFromFileClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Graph JSON");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Graph Files (*.json, *.graph)", "*.json", "*.graph"),
            new FileChooser.ExtensionFilter("JSON Files", "*.json"),
            new FileChooser.ExtensionFilter("Graph Files", "*.graph"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                Path path = file.toPath();
                graph = GraphParser.parseFromFile(path);
                sorter = new TopologicalSorter(graph);
                calculateScaling();
                drawGraph();
                updatePathLabel();
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Error", "Load failed", e.getMessage());
            }
        }
    }

    private void calculateScaling() {
        if (graph == null || graph.getVertices().isEmpty()) return;
        
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        
        for (Vertex v : graph.getVertices()) {
            minX = Math.min(minX, v.getX());
            maxX = Math.max(maxX, v.getX());
            minY = Math.min(minY, v.getY());
            maxY = Math.max(maxY, v.getY());
        }
        
        double graphWidth = maxX - minX;
        double graphHeight = maxY - minY;
        
        // Получаем реальные размеры с учетом ScrollPane
        double canvasWidth = graphCanvas.getWidth();
        double canvasHeight = graphCanvas.getHeight();
        
        // Добавляем 20% отступ для раннего появления скролла
        double paddingFactor = 0.2;
        double scaleX = (canvasWidth > 0 && graphWidth > 0) 
                ? (canvasWidth * (1 - paddingFactor)) / graphWidth : 1;
        double scaleY = (canvasHeight > 0 && graphHeight > 0) 
                ? (canvasHeight * (1 - paddingFactor)) / graphHeight : 1;
        
        scale = Math.min(scaleX, scaleY) * 0.95; // Добавляем небольшой отступ
        
        // Центрирование графа
        offsetX = (canvasWidth - graphWidth * scale) / 2 - minX * scale;
        offsetY = (canvasHeight - graphHeight * scale) / 2 - minY * scale;
    }

    private double transformX(double x) {
        return offsetX + x * scale;
    }

    private double transformY(double y) {
        return offsetY + y * scale;
    }

    private void drawGraph() {
        graphCanvas.getChildren().clear();
        vertexNodes.clear();
        
        // Рисуем рёбра со стрелками
        for (Vertex vertex : graph.getVertices()) {
            double x1 = transformX(vertex.getX());
            double y1 = transformY(vertex.getY());
            
            for (Vertex neighbor : vertex.getNeighbors()) {
                double x2 = transformX(neighbor.getX());
                double y2 = transformY(neighbor.getY());
                
                // Рассчитываем вектор направления
                double dx = x2 - x1;
                double dy = y2 - y1;
                double length = Math.sqrt(dx * dx + dy * dy);
                
                if (length < 1e-6) continue;
                
                // Нормализуем вектор
                dx /= length;
                dy /= length;
                
                // Отступаем от конечной точки
                double endX = x2 - 20 * dx;
                double endY = y2 - 20 * dy;
                
                // Отрисовка линию
                Line edgeLine = new Line(x1, y1, endX, endY);
                edgeLine.setStrokeWidth(2);
                graphCanvas.getChildren().add(edgeLine);
                
                // Отрисовка стрелку
                drawArrow(endX, endY, dx, dy);
            }
        }
        
        // Рисуем вершины
        for (Vertex vertex : graph.getVertices()) {
            double x = transformX(vertex.getX());
            double y = transformY(vertex.getY());
            
            Circle node = new Circle(x, y, 20);
            node.setStyle("-fx-fill: lightgray; -fx-stroke: black; -fx-stroke-width: 2;");
            
            Text label = new Text(x - 5, y + 5, vertex.getName());
            label.setStyle("-fx-font-weight: bold;");

            graphCanvas.getChildren().addAll(node, label);
            vertexNodes.put(vertex, node);
        }
    }

    // Метод для рисования стрелки
    private void drawArrow(double endX, double endY, double dx, double dy) {
        double arrowLength = 12;
        double arrowAngle = Math.toRadians(30);
        
        double x1 = endX - arrowLength * (dx * Math.cos(arrowAngle) + dy * Math.sin(arrowAngle));
        double y1 = endY - arrowLength * (dy * Math.cos(arrowAngle) - dx * Math.sin(arrowAngle));
        
        double x2 = endX - arrowLength * (dx * Math.cos(-arrowAngle) + dy * Math.sin(-arrowAngle));
        double y2 = endY - arrowLength * (dy * Math.cos(-arrowAngle) - dx * Math.sin(-arrowAngle));
        
        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(endX, endY, x1, y1, x2, y2);
        arrowHead.setStyle("-fx-fill: black;");
        
        graphCanvas.getChildren().add(arrowHead);
    }
    
    @FXML
    void onForwardButtonClick(ActionEvent event) {
        if (sorter == null) {
            showAlert(AlertType.WARNING, "Warning", "No graph", "Load a graph first");
            return;
        }
        
        if (sorter.hasNext()) {
            Vertex current = sorter.next();
            highlightVertex(current, "green");
            updatePathLabel();
        } else {
            showAlert(AlertType.INFORMATION, "Complete", "Sorting finished", "All vertices processed");
        }
    }

    @FXML
    void onBackButtonClick(ActionEvent event) {
        if (sorter == null) {
            showAlert(AlertType.WARNING, "Warning", "No graph", "Load a graph first");
            return;
        }
        
        if (!sorter.getSortedSoFar().isEmpty()) {
            Vertex current = sorter.prev();
            highlightVertex(current, "lightgray");
            updatePathLabel();
        }
    }

    @FXML
    void onRunImmediatelyClick(ActionEvent event) {
        if (sorter == null) {
            showAlert(AlertType.WARNING, "Warning", "No graph", "Load a graph first");
            return;
        }
        
        while (sorter.hasNext()) {
            Vertex current = sorter.next();
            highlightVertex(current, "green");
        }
        updatePathLabel();
    }

    private void highlightVertex(Vertex vertex, String color) {
        Circle node = vertexNodes.get(vertex);
        if (node != null) {
            node.setStyle("-fx-fill: " + color + "; -fx-stroke: black; -fx-stroke-width: 2;");
        }
    }

    // Обновление отображения пути
    private void updatePathLabel() {
        if (sorter == null || sorter.getSortedSoFar().isEmpty()) {
            pathLabel.setText("Topological Path: ");
            return;
        }
        
        StringBuilder path = new StringBuilder("Topological Path: ");
        for (Vertex v : sorter.getSortedSoFar()) {
            path.append(v.getName()).append(" → ");
        }
        path.setLength(path.length() - 3); // Удаляем последнюю стрелку
        pathLabel.setText(path.toString());
    }

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}