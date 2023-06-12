package com.antoher.oop.controllers;

import com.antoher.oop.data.IO.CReader;
import com.antoher.oop.data.IO.CWriter;
import com.antoher.oop.data.Mapper;
import com.antoher.oop.models.BinaryHeap;
import com.antoher.oop.models.CCity;
import com.antoher.oop.views.HeapView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.antoher.oop.data.IO.FileUtils.getFileExtension;

public class MainController {
    public static final String ERROR_COLOR = "-fx-border-color: #FFCCCC;";
    private File currentFile = null;

    @FXML
    HeapView<CCity> heapView;

    @FXML
    Label heapSize;
    @FXML
    Label airportCount;

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
    private TextField areaChange;
    @FXML
    private TextField cityNameChange;
    @FXML
    private TextField populationChange;
    @FXML
    private CheckBox hasAirportChange;

    @FXML
    private TitledPane changePane;

    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private ToggleButton sortMode;
    @FXML
    private CheckBox rawHeapMode;
    @FXML
    private FontAwesomeIconView faSort;
    @FXML
    private Button deleteItem;

    public MainController() {

    }

    @FXML
    void initialize() {
        initListeners();
        heapView.setBinaryHeap(cities);

        sortMode.selectedProperty().addListener((observable, oldValue, newValue) -> {
            heapView.setAscendingSort(!newValue);
            faSort.setIcon(newValue ? FontAwesomeIcon.SORT_AMOUNT_DESC : FontAwesomeIcon.SORT_AMOUNT_ASC);
        });

        ObservableList<String> columnNames = heapView.getColumns().stream()
                .map(TableColumn::getText)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        sortComboBox.setItems(columnNames);
        sortComboBox.setOnAction(event -> {
            String selectedColumnName = sortComboBox.getValue();
            CCity.setSortColumn(selectedColumnName);
            if (!sortMode.isSelected()) {
                heapView.resortHeap();
            } else {
                heapView.reverseHeap();
            }
        });
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

        isValid = validateTextField(area, isValid);
        isValid = validateTextField(cityName, isValid);
        isValid = validateTextField(population, isValid);

        if (isValid) {
            CCity city = new CCity(Integer.parseInt(population.getText()),
                    Double.parseDouble(area.getText()),
                    cityName.getText(),
                    hasAirport.isSelected());
            cities.insert(city);
            clearFields();
        }
    }

    public void handleChangeCityButtonAction() {
        CCity selectedElement = heapView.getSelectionModel().getSelectedItem();

        boolean isValid = selectedElement != null;

        isValid = validateTextField(areaChange, isValid);
        isValid = validateTextField(cityNameChange, isValid);
        isValid = validateTextField(populationChange, isValid);

        if (isValid) {
            String newCityName = cityNameChange.getText();
            int newPopulation = Integer.parseInt(populationChange.getText());
            double newArea = Double.parseDouble(areaChange.getText());
            CCity newElement = new CCity(selectedElement);
            newElement.setArea(newArea);
            newElement.setName(newCityName);
            newElement.setPopulation(newPopulation);
            newElement.setHasAirport(hasAirportChange.isSelected());
            heapView.updateItem(selectedElement, newElement);
            heapView.refresh();
        }
    }

    public void handleDeleteCityButtonAction(ActionEvent event) {
        CCity selectedElement = heapView.getSelectionModel().getSelectedItem();
        cities.delete(selectedElement);
    }

    private void clearFields() {
        cityName.clear();
        population.clear();
        area.clear();
        hasAirport.setSelected(false);
    }

    private void clearChangeFields() {
        cityNameChange.clear();
        populationChange.clear();
        areaChange.clear();
        hasAirportChange.setSelected(false);
    }

    private void initListeners() {
        heapView.setSortPolicy(event -> false);
        cities.addListener(c -> {
            heapView.refresh();
            heapSize.setText(String.valueOf(heapView.getLength()));
            airportCount.setText(String.valueOf(CCity.getTotalAirports()));
        });

        heapView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                changePane.setExpanded(true);
                areaChange.setText(String.valueOf(newValue.getArea()));
                cityNameChange.setText(newValue.getName());
                populationChange.setText(String.valueOf(newValue.getPopulation()));
                hasAirportChange.setSelected(newValue.isHasAirport());
            } else {
                clearChangeFields();
            }
        });

        area.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(area, newValue));
        population.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(population, newValue));
        areaChange.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(areaChange, newValue));
        populationChange.textProperty().addListener((observable, oldValue, newValue) -> validateNumericInput(populationChange, newValue));

        rawHeapMode.selectedProperty().addListener((observable, oldValue, newValue) -> heapView.setRawHeapMode(newValue));
    }

    private void validateNumericInput(TextField textField, String newValue) {
        if (!newValue.matches("\\d*(\\.\\d*)?")) {
            textField.setText(newValue.replaceAll("[^\\d.]", ""));
        }
    }

    private boolean validateTextField(TextField textField, boolean isValid) {
        if (textField.getText().isEmpty()) {
            textField.setStyle(ERROR_COLOR);
            isValid = false;
        } else {
            textField.setStyle("");
        }
        return isValid;
    }

    private boolean readFile(File file) {
        String extension = getFileExtension(file.getName());
        if (extension.equalsIgnoreCase("csv") || extension.equalsIgnoreCase("txt")) {
            CReader reader = new CReader();
            List<Map<String, Object>> map = reader.readToMap(file);
            try {
                cities.setRefreshMode(BinaryHeap.RefreshMode.NEVER);
                cities.clear();
                for (Map<String, Object> objectMap : map) {
                    cities.insert(Mapper.mapToObject(objectMap, CCity.class));
                }
                currentFile = file;
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException |
                     NoSuchMethodException | RuntimeException e) {
                showWarningAlert("Ошибка импорта файла", "Ошибка",
                        "Файл не соответствует шаблону класса: " + CCity.class.getName());
                return false;
            } finally {
                heapView.refresh();
                heapSize.setText(String.valueOf(heapView.getLength()));
                airportCount.setText(String.valueOf(CCity.getTotalAirports()));
                cities.setRefreshMode(BinaryHeap.RefreshMode.ALWAYS);
            }
        } else {
            showWarningAlert("Ошибка импорта файла", "Ошибка",
                    "Файл не соответствует шаблону класса: " + CCity.class.getName());
            return false;
        }
        return true;
    }

    @FXML
    private void saveFile(ActionEvent event) {
        if (currentFile != null) {
            saveDataToFile(currentFile);
        } else {
            saveAsFile(event);
        }
    }

    @FXML
    private void saveAsFile(ActionEvent event) {
        Window window = heapView.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            saveDataToFile(file);
            currentFile = file;
        }
    }

    private void saveDataToFile(File file) {
        CWriter writer = new CWriter();
        List<Map<String, Object>> objects = new ArrayList<>();

        for (CCity city : cities) {
            try {
                objects.add(Mapper.objectToMap(city));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        writer.writeFromMap(file, objects);
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
        Window window = heapView.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Выберите файл");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            readFile(file);
        }
    }

    public void closeWindow(ActionEvent event) {
        Platform.exit();
    }
}