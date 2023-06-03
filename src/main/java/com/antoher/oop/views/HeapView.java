package com.antoher.oop.views;

import com.antoher.oop.annotations.ColumnName;
import com.antoher.oop.models.BinaryHeap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HeapView<T extends Comparable<T>> extends TableView<T> {

    private BinaryHeap<T> binaryHeap;

    public HeapView() {
        super();
    }

    public HeapView(BinaryHeap<T> binaryHeap) {
        setBinaryHeap(binaryHeap);
    }

    public BinaryHeap<T> getBinaryHeap() {
        return binaryHeap;
    }

    public void setBinaryHeap(BinaryHeap<T> binaryHeap) {
        this.binaryHeap = binaryHeap;
        initializeColumns();
        populateData();
    }

    private void initializeColumns() {
        Class<?> itemClass = binaryHeap.getItemClass();
        java.lang.reflect.Field[] fields = itemClass.getDeclaredFields();

        for (java.lang.reflect.Field field : fields) {
            if (field.isAnnotationPresent(ColumnName.class)) {
                ColumnName annotation = field.getAnnotation(ColumnName.class);
                String columnName = annotation.name();

                TableColumn<T, Object> column = new TableColumn<>(columnName);
                column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));

                getColumns().add(column);
            }
        }
    }

    private void populateData() {
        T[] elements = binaryHeap.getHeap();
        ObservableList<T> observableList = FXCollections.observableArrayList(elements);

        setItems(observableList);
    }
}
