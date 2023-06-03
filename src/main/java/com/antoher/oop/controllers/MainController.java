package com.antoher.oop.controllers;

import com.antoher.oop.models.BinaryHeap;
import com.antoher.oop.models.CCity;
import com.antoher.oop.views.HeapView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class MainController {

    @FXML
    HeapView<CCity> heapView;
    ObservableList<CCity> cities = FXCollections.observableArrayList();
    BinaryHeap<CCity> citiesH = new BinaryHeap<>(CCity.class);

    public MainController() {

    }

    @FXML
    void initialize() {
        citiesH.insert(new CCity(25000, 250000, "Nizhnevartovsk", true));
        citiesH.insert(new CCity(2500000, 250000, "St. Petersburg", true));
        citiesH.insert(new CCity(1000000, 250000, "Tyumen", true));
        citiesH.insert(new CCity(3213, 321321, "Tyumen2", false));

        heapView.setBinaryHeap(citiesH);
    }
}