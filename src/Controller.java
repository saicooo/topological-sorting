import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
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

    @FXML private ScrollPane scrollPane;
    @FXML private Pane graphCanvas;
    
    // Кнопки (без изменений)
    @FXML private Button BackButton;
    @FXML private Button EnterManually;
    @FXML private Button ForwardButton;
    @FXML private Button OnLoadFromFile;
    @FXML private Button RunImmediately;

    private Graph graph;
    private TopologicalSorter sorter;
    private final Map<Vertex, Circle> vertexNodes = new HashMap<>();
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private double offsetX = 0;
    private double offsetY = 0;

    @FXML
    void onLoadFromFileClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Graph JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                Path path = file.toPath();
                graph = GraphParser.parseFromFile(path);
                sorter = new TopologicalSorter(graph);
                calculateScaling();
                drawGraph();
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Error", "Load failed", e.getMessage()); // Исправлен вызов
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
        double canvasWidth = 1200;
        double canvasHeight = 800;
        
        scaleX = (graphWidth > 0) ? (canvasWidth - 100) / graphWidth : 1;
        scaleY = (graphHeight > 0) ? (canvasHeight - 100) / graphHeight : 1;
        double scale = Math.min(scaleX, scaleY);
        scaleX = scale;
        scaleY = scale;
        
        offsetX = 50 - minX * scaleX;
        offsetY = 50 - minY * scaleY;
    }

    private double transformX(double x) {
        return offsetX + x * scaleX;
    }

    private double transformY(double y) {
        return offsetY + y * scaleY;
    }

    private void drawGraph() {
        graphCanvas.getChildren().clear();
        vertexNodes.clear();
        
        double maxX = 0;
        double maxY = 0;
        
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
                
                if (length < 1e-6) continue; // избегаем деления на ноль
                
                // Нормализуем вектор
                dx /= length;
                dy /= length;
                
                // Отступаем от конечной точки на радиус вершины
                double endX = x2 - 20 * dx;
                double endY = y2 - 20 * dy;
                
                // Отрисовываем линию
                Line edgeLine = new Line(x1, y1, endX, endY);
                edgeLine.setStrokeWidth(2);
                graphCanvas.getChildren().add(edgeLine);
                
                // Отрисовываем стрелку
                drawArrow(endX, endY, dx, dy);
                
                maxX = Math.max(maxX, Math.max(x1, x2));
                maxY = Math.max(maxY, Math.max(y1, y2));
            }
        }
        
        // Рисуем вершины (без изменений)
        for (Vertex vertex : graph.getVertices()) {
            double x = transformX(vertex.getX());
            double y = transformY(vertex.getY());
            
            Circle node = new Circle(x, y, 20);
            node.setStyle("-fx-fill: lightgray; -fx-stroke: black; -fx-stroke-width: 2;");
            
            Text label = new Text(x - 5, y + 5, vertex.getName());
            label.setStyle("-fx-font-weight: bold;");

            graphCanvas.getChildren().addAll(node, label);
            vertexNodes.put(vertex, node);
            
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }
        
        graphCanvas.setMinSize(maxX + 50, maxY + 50);
        graphCanvas.setPrefSize(maxX + 50, maxY + 50);
    }

    // Метод для рисования стрелки
    private void drawArrow(double endX, double endY, double dx, double dy) {
        double arrowLength = 12; // длина "крыльев" стрелки
        double arrowAngle = Math.toRadians(30); // угол наклона крыльев
        
        // Вычисляем точки для стрелки
        double x1 = endX - arrowLength * (dx * Math.cos(arrowAngle) + dy * Math.sin(arrowAngle));
        double y1 = endY - arrowLength * (dy * Math.cos(arrowAngle) - dx * Math.sin(arrowAngle));
        
        double x2 = endX - arrowLength * (dx * Math.cos(-arrowAngle) + dy * Math.sin(-arrowAngle));
        double y2 = endY - arrowLength * (dy * Math.cos(-arrowAngle) - dx * Math.sin(-arrowAngle));
        
        // Создаем треугольник-стрелку
        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(
            endX, endY, // острие стрелки
            x1, y1,     // первое крыло
            x2, y2      // второе крыло
        );
        arrowHead.setStyle("-fx-fill: black;"); // цвет стрелки
        
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
    }

    @FXML
    void onInputManualClick(ActionEvent event) {
        showAlert(AlertType.INFORMATION, "Manual Input", "Coming Soon", "This feature will be implemented later");
    }

    private void highlightVertex(Vertex vertex, String color) {
        Circle node = vertexNodes.get(vertex);
        if (node != null) {
            node.setStyle("-fx-fill: " + color + "; -fx-stroke: black; -fx-stroke-width: 2;");
        }
    }

    // Исправленная сигнатура метода showAlert
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}