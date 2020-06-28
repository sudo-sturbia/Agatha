package com.github.sudo_sturbia.agatha.client.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class View extends Application
{
    public static void main(String[] args)
    {
        View.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        GridPane root = FXMLLoader.load(View.class.getResource("layouts/baseLayout.fxml"));
        GridPane userLogin = FXMLLoader.load(View.class.getResource("layouts/loginLayout.fxml"));

        root.add(userLogin, 0, 0);

        Font.loadFont(View.class.getResource("fonts/Lato-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(View.class.getResource("fonts/AbrilFatface-Regular.ttf").toExternalForm(), 10);

        Scene scene = new Scene(root, 1000, 750);
        scene.getStylesheets().add(View.class.getResource("style/style.css").toExternalForm());

        primaryStage.setTitle("Agatha");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
