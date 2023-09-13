package com.example.desktop_database_application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    static Stage stage;
    static Driver driver;

    @FXML TextField username, password;
    @FXML Label loginFailure;

    @FXML
    public void signInAction(ActionEvent event) throws Exception{
        String user = username.getText();
        String pass = password.getText();

        if (!user.equals("user") || !pass.equals("pass")){
            loginFailure.setVisible(true);
            return;
        }

        // successful sign in
        driver.goToQueryScreen(stage);
    }

    public LoginController(Stage inputStage, Driver inputDriver){
        stage = inputStage;
        driver = inputDriver;
    }
}
