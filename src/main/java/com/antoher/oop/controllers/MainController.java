package com.antoher.oop.controllers;

import com.antoher.oop.data.IO.CReader;
import com.antoher.oop.data.Mapper;
import com.antoher.oop.models.BinaryHeap;
import com.antoher.oop.models.CCity;
import com.antoher.oop.views.HeapView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.antoher.oop.data.IO.FileUtils.getFileExtension;

public class MainController {

    public static final String ERROR_COLOR = "-fx-border-color: #FFCCCC;";
    @FXML
    HeapView<CCity> heapView;
//    @FXML
//    TableView<CCity> heapView;

    @FXML
    Label heapSize;

    BinaryHeap<CCity> cities = new BinaryHeap<>(CCity.class);

    @FXML
    private TextField area;
    @FXML
    private TextField cityName;
    @FXML
    private TextField population;
    @FXML
    private CheckBox hasAirport;
    @FXML
    private Button addCity;

    public MainController() {

    }

    @FXML
    void initialize() {
        initListeners();
        heapView.setBinaryHeap(cities);
    }

    @FXML
    void onDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            heapView.setStyle("-fx-border-color: #139CFF; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-style: dashed; " +
                    "-fx-border-insets: 5;" +
                    "-fx-background-color: #00000012");
        }
        event.consume();
    }

    @FXML
    void onDragExited(DragEvent event) {
        heapView.setStyle("");
        event.consume();
    }

    @FXML
    void onDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;
        if (dragboard.hasFiles()) {
            List<File> files = dragboard.getFiles();
            for (File file : files) {
                success = readFile(file);
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    public void handleAddCityButtonAction() {
        boolean isValid = true;

        if (area.getText().isEmpty()) {
            area.setStyle(ERROR_COLOR);
            isValid = false;
        } else {
            area.setStyle("");
        }

        if (cityName.getText().isEmpty()) {
            cityName.setStyle(ERROR_COLOR);
            isValid = false;
        } else {
            cityName.setStyle("");
        }

        if (population.getText().isEmpty()) {
            population.setStyle(ERROR_COLOR);
            isValid = false;
        } else {
            population.setStyle("");
        }

        if (isValid) {
            CCity city = new CCity(Integer.parseInt(population.getText()),
                    Double.parseDouble(area.getText()),
                    cityName.getText(),
                    hasAirport.isSelected());
            cities.insert(city);
            clearFields();
        }
    }

    private void clearFields() {
        cityName.clear();
        population.clear();
        area.clear();
        hasAirport.setSelected(false);
    }

    private void initListeners() {
        heapView.setSortPolicy(event -> false);
        cities.addListener(c -> {
            heapView.refresh();
            heapSize.setText(String.valueOf(heapView.getLength()));
        });

        area.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                area.setText(oldValue);
            }
        });

        population.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                population.setText(oldValue);
            }
        });
    }

    private boolean readFile(File file) {
        String extension = getFileExtension(file.getName());
        if (extension.equalsIgnoreCase("csv") || extension.equalsIgnoreCase("txt")) {
            CReader reader = new CReader();
            List<Map<String, Object>> map = reader.readToMap(file);
            try {
                for (Map<String, Object> objectMap : map) {
                    cities.insert(Mapper.mapToObject(objectMap, CCity.class));
                }

            } catch (InvocationTargetException | IllegalAccessException | InstantiationException |
                     NoSuchMethodException | RuntimeException e) {
                showWarningAlert("Ошибка импорта файла", "Ошибка",
                        "Файл не соответствует шаблону класса: " + CCity.class.getName());
                return false;
            }
        } else return false;
        return true;
    }

    private void showWarningAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.initModality(Modality.APPLICATION_MODAL);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            alert.close();
        }
    }

    public void openFileWindow(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Выберите файл");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            readFile(file);
        }
    }

    public void closeWindow(ActionEvent event) {
        Platform.exit();
    }
}