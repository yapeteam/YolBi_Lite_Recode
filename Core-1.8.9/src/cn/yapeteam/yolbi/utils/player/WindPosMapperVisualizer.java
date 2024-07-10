package cn.yapeteam.yolbi.utils.player;

import cn.yapeteam.yolbi.utils.vector.Vector2f;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.List;

public class WindPosMapperVisualizer extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("WindPosMapper Visualizer");

        // Create start and end points
        Vector2f start = new Vector2f(-370, 90.0f);
        Vector2f end = new Vector2f(1800, 90.0f);

        // Define rotation speed
        double rotationSpeed = 10.0;

        // Generate path
        List<Vector2f> path = WindPosMapper.generatePath(start, end);// WindPosMapper.generatePath(start, end);
        System.out.println("Generated path with " + path.size());

        // Create dataset
        XYChart.Series series = createDataset(path);

        // Defining the axes
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        // Creating the scatter chart
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);

        // Prepare ScatterChart.Series objects by setting data
        scatterChart.getData().add(series);

        // Creating a scene object
        Scene scene = new Scene(scatterChart, 800, 400);

        // Setting title to the Stage
        stage.setTitle("Scatter plot");

        // Adding scene to the stage
        stage.setScene(scene);

        // Displaying the contents of the stage
        stage.show();
    }

    private XYChart.Series createDataset(List<Vector2f> path) {
        XYChart.Series series = new XYChart.Series();

        for (Vector2f point : path) {
            series.getData().add(new XYChart.Data(point.x, point.y));
        }

        return series;
    }

    public static void main(String[] args) {
        launch(args);
    }
}