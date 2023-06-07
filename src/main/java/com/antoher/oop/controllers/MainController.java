package com.antoher.oop.controllers;

import com.antoher.oop.data.IO.CReader;
import com.antoher.oop.data.Mapper;
import com.antoher.oop.models.BinaryHeap;
import com.antoher.oop.models.CCity;
import com.antoher.oop.views.HeapView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
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

    @FXML
    HeapView<CCity> heapView;
//    @FXML
//    TableView<CCity> heapView;

    @FXML
    Label heapSize;

    BinaryHeap<CCity> cities = new BinaryHeap<>(CCity.class);

    public MainController() {

    }

    @FXML
    void initialize() {
        cities.addListener(c -> {
            heapView.refresh();
            heapSize.setText(String.valueOf(heapView.getLength()));
        });
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
        }
        else return false;
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