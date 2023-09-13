package com.example.desktop_database_application;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class ManageUserController {
    static Stage stage;
    static Driver driver;

    @FXML TextField addIDField, suspendIDField, activateIDField;
    @FXML Label addIDFailure, suspendIDFailure, activateIDFailure, queryResultText;
    @FXML CheckBox activateBox;
    @FXML RadioButton addStudentOption, addFacultyOption;

    public void addButtonPressed(){
        int id = QueryScreenController.parseID(addIDField.getText());
        if (id == 0){
            addIDFailure.setVisible(true);
            queryResultText.setVisible(false);
            return;
        } else if (Driver.doesIDExist(id)){
            addIDFailure.setVisible(false);
            queryResultText.setText("User already exists!");
            queryResultText.setVisible(true);
            return;
        }
        addIDFailure.setVisible(false);

        StringBuffer query = new StringBuffer();
        query.append("INSERT INTO users VALUES (");
        query.append(id);

        if(addStudentOption.isSelected()){
            query.append(", 'Student', ");
        } else {
            query.append(", 'Faculty', ");
        }

        if (activateBox.isSelected()){
            query.append("1);");
        } else {
            query.append("0);");
        }

        tryQuery(query.toString(), 1);
    }

    public void suspendButtonPressed(){
        int id = QueryScreenController.parseID(suspendIDField.getText());
        if (id == 0){
            suspendIDFailure.setVisible(true);
            queryResultText.setVisible(false);
            return;
        } else if (!Driver.doesIDExist(id)){
            suspendIDFailure.setVisible(false);
            queryResultText.setText("User does not exist!");
            queryResultText.setVisible(true);
            return;
        }
        suspendIDFailure.setVisible(false);

        StringBuffer query = new StringBuffer();
        query.append("update users set hasAccess = 0 where id = ");
        query.append(id);
        query.append(";");

        tryQuery(query.toString(), 2);
    }

    public void activateButtonPressed(){
        int id = QueryScreenController.parseID(activateIDField.getText());
        if (id == 0){
            activateIDFailure.setVisible(true);
            queryResultText.setVisible(false);
            return;
        } else if (!Driver.doesIDExist(id)){
            activateIDFailure.setVisible(false);
            queryResultText.setText("User does not exist!");
            queryResultText.setVisible(true);
            return;
        }
        activateIDFailure.setVisible(false);

        StringBuffer query = new StringBuffer();
        query.append("update users set hasAccess = 1 where id = ");
        query.append(id);
        query.append(";");

        tryQuery(query.toString(), 3);
    }

    public void tryQuery(String query, int type){
        try {
            //execute query
            Driver.executeQuery(query);
            if (type == 1){
                queryResultText.setText("User successfully added!");
            } else if (type == 2){
                queryResultText.setText("User successfully suspended!");
            } else if (type == 3){
                queryResultText.setText("User successfully activated!");
            }
        } catch (Exception e){
            if (type == 1){
                queryResultText.setText("Unable to add user.");
            } else if (type == 2){
                queryResultText.setText("Unable to suspend user.");
            } else if (type == 3){
                queryResultText.setText("Unable to activate user.");
            }
        }
        queryResultText.setVisible(true);
    }

    public void goBack() throws Exception{
        driver.goToQueryScreen(stage);
    }

    public void facultyButtonClicked(){
        addStudentOption.setSelected(false);
        addFacultyOption.setSelected(true);
    }

    public void studentButtonClicked(){
        addFacultyOption.setSelected(false);
        addStudentOption.setSelected(true);
    }

    public ManageUserController(Stage inputStage, Driver inputDriver){
        stage = inputStage;
        driver = inputDriver;
    }
}