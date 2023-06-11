package com.antoher.oop.views;

import com.antoher.oop.annotations.ColumnName;
import com.antoher.oop.models.BinaryHeap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Collections;

public class HeapView<T extends Comparable<T>> extends TableView<T> {

    private BinaryHeap<T> binaryHeap;

    private boolean rawHeapMode = true;
    private boolean ascendingSort = true;

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

    @Override
    public void refresh() {
        populateData();
    }

    public boolean isRawHeapMode() {
        return rawHeapMode;
    }

    public void setRawHeapMode(boolean rawHeapMode) {
        this.rawHeapMode = rawHeapMode;
        refresh();
    }

    public boolean isAscendingSort() {
        return ascendingSort;
    }

    public void setAscendingSort(boolean ascendingSort) {
        this.ascendingSort = ascendingSort;
        refresh();
    }

    public void resortHeap() {
        binaryHeap.resortHeap();
        refresh();
    }

    public void reverseHeap() {
        binaryHeap.reverseHeap();
        refresh();
    }

    public int getLength(){
        return binaryHeap.getSize();
    }

    private void initializeColumns() {
        Class<?> itemClass = binaryHeap.getItemClass();
        java.lang.reflect.Field[] fields = itemClass.getDeclaredFields();

        for (java.lang.reflect.Field field : fields) {
            if (field.isAnnotationPresent(ColumnName.class)) {
                ColumnName annotation = field.getAnnotation(ColumnName.class);
                String columnName = annotation.name();
                boolean collapse = annotation.collapsed();
                TableColumn<T, Object> column = new TableColumn<>(columnName);
                column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
                column.setVisible(!collapse);
                getColumns().add(column);
            }
        }
    }

    public void updateItem(T oldItem, T newItem) {
        binaryHeap.update(oldItem, newItem);
    }

    private void populateData() {
        T[] elements = binaryHeap.getHeap();
        if(!isRawHeapMode()){
            BinaryHeap.heapify(elements);
        }

        ObservableList<T> observableList = FXCollections.observableArrayList(elements);
        if(!isAscendingSort())
            Collections.reverse(observableList);
        setItems(observableList);
    }
}
