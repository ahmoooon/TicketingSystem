module TicketingSystem {
    requires javafx.base;
    requires java.logging;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    requires jdk.jsobject;
    requires jfx.incubator.input;
    requires jfx.incubator.richtext;
    
    requires com.google.zxing;
    requires com.google.zxing.javase;

    exports presentation.gui;
    exports presentation.gui.views;
    exports presentation.cli;
    exports application.services;
    exports application.dto;
    exports application.strategies;
    exports application.utilities;
    exports domain.repositories;
    exports domain;
    exports domain.factory;
    exports domain.valueobjects;
    exports infrastructure.repositories;
    exports infrastructure.strategies;

    opens presentation.gui to javafx.fxml;
}