package com.example.desktop_database_application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDate;

public class QueryScreenController {
    static Stage stage;
    static Driver driver;

    @FXML Pane dateFilterPanel;
    @FXML TextField userIDField, startTime, endTime;
    @FXML CheckBox userIDFilterBox, dateFilterBox, orderBox;
    @FXML Label studIDFailure, queryFailureText;
    @FXML DatePicker dateInput;
    @FXML ListView<String> queryResults;

    static public int parseID(String id){
        try {
            if(id.length() != 9){
                throw new Exception();
            }
            return Integer.parseInt(id);
        } catch (Exception e){
            return 0;
        }
    }

    // build the query based on the fields and boxes, call on the driver to execute it, and display the results
    public void execQuery(ActionEvent event) throws Exception{
        queryResults.setVisible(false);
        queryResults.getItems().clear();
        String studentInput;
        studentInput = userIDField.getText();
        int studentID = parseID(studentInput);

        if(userIDFilterBox.isSelected() && (studentInput.equals("") || parseID(studentInput) == 0)){
            studIDFailure.setVisible(true);
            return;
        }
        // hide the failure message upon a successful query
        studIDFailure.setVisible(false);

        StringBuffer query = new StringBuffer();
        String queryTable;
        if (dateFilterBox.isSelected() || userIDFilterBox.isSelected()){
            query.append("SELECT * FROM access");
            queryTable = "access";
        } else {
            query.append("SELECT * FROM users");
            queryTable = "users";
        }

        if (studentID != 0){
            query.append(" WHERE id = ");
            query.append(studentID);
        }

        LocalDate date = dateInput.getValue();
        String startTimeString = startTime.getText();
        String endTimeString = endTime.getText();

        if (date != null){
            if (studentID != 0){
                query.append(" AND");
            } else {
                query.append(" WHERE");
            }

            query.append(" date = '");
            query.append(date.toString());
            query.append("'");

            if (!startTimeString.equals("")){
                if (!isTimeValid(startTimeString)){
                    queryFailureText.setText("Invalid start time!");
                    queryFailureText.setVisible(true);
                    return;
                }

                query.append(" AND time >= '");
                query.append(startTimeString);
                query.append(":00'");
            }

            if (!endTimeString.equals("")){
                if (!isTimeValid(endTimeString)){
                    queryFailureText.setText("Invalid end time!");
                    queryFailureText.setVisible(true);
                    return;
                }

                query.append(" AND time <= '");
                query.append(endTimeString);
                query.append(":00'");
            }
        }

        // only filter by the student IDs if only the student filter box is checked
        if (!userIDFilterBox.isSelected() && !dateFilterBox.isSelected()){
            query.append(" ORDER BY id");
        } else {
            query.append(" ORDER BY date, time");
        }

        if (orderBox.isSelected()){
            query.append(" ASC;");
        } else {
            query.append(" DESC;");
        }

        //execute query
        try {
            //System.out.println(query); // debug code
            ResultSet results = Driver.executeQuery(query.toString());
            ResultSetMetaData metaData = results.getMetaData();
            int numCols = metaData.getColumnCount();
            while (results.next()){
                StringBuffer entry = new StringBuffer();
                for (int i = 1; i <= numCols; i++){

                    if ((i == 3 && queryTable.equals("users")) || (i == 2 && queryTable.equals("access"))){
                        entry.append(translateEntry(queryTable, results.getObject(i).toString()));
                    } else {
                        entry.append(results.getObject(i));
                    }
                    entry.append("\t\t");
                }
                queryResults.getItems().add(entry.toString());
            }
        } catch (Exception e){
            queryFailureText.setText("Unable to query data!");
            queryFailureText.setVisible(true);
            return;
        }

        queryFailureText.setVisible(false);
        queryResults.setVisible(true);
    }

    // translates 0/1 entries in the table into what they actually symbolize for readability
    public String translateEntry(String table, String entry){
        if (table.equals("users")){
            if (entry.equals("0")){
                return "Suspended";
            }
            return "Activated";
        }
        if (entry.equals("0")){
            return "Exit   ";
        }
        return "Enter";
    }

    public boolean isTimeValid(String time){
        if(time.length() != 5){
            return false;
        }

        try {
            int hour = Integer.parseInt(time.substring(0,2));
            int minute = Integer.parseInt(time.substring(3,5));

            return minute >= 0 && minute <= 59 && hour >= 0 && hour <= 23;
        }catch (Exception ignored){}
        return false;
    }

    public void usersButtonPressed() throws Exception{
        driver.showManageUsersPanel(stage);
    }

    // toggles if the date UI is visible when the respective box is checked
    public void showDateUI(ActionEvent event){
        if(dateFilterBox.isSelected()){
            dateFilterPanel.setVisible(true);
            return;
        }
        dateFilterPanel.setVisible(false);
        dateInput.setValue(null);
        startTime.setText("");
        endTime.setText("");
    }

    public void showIDField(ActionEvent event){
        if(userIDFilterBox.isSelected()){
            userIDField.setVisible(true);
            return;
        }
        userIDField.setVisible(false);
        userIDField.setText("");
    }

    public QueryScreenController(Stage inputStage, Driver inputDriver){
        stage = inputStage;
        driver = inputDriver;
    }
}
