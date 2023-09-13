package com.example.desktop_database_application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Scanner;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.*;

public class Driver extends Application{
    static final String DATABASE_URL = "jdbc:sqlite:identifier.sqlite"; // PASTE DATABASE URL HERE
    static Connection connection;

    public static void main(String[] args) throws Exception{
        establishConnection();
        System.out.println("Select access:\n1. Student\n2. Faculty\n3. Admin\n");
        int choice = getChoice(1, 3);
        switch (choice){
            case 1:
            case 2: //students and faculty are treated identically
                int id = promptID();
                if (id == 0){
                    break;
                } else if (!doesIDExist(id)){
                    System.out.println("User not found, contact the admin for access.");
                } else if (!hasAccess(id) && isEntering(id)){ // make sure suspended users can't get trapped inside
                    System.out.println("Access denied. User is suspended.");
                } else {
                    handleCardSwipe(id);
                }
                break;
            case 3:
                launch();
        }

        System.exit(0);
    }

    public static void establishConnection(){
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
        } catch (Exception e){
            System.err.println("Error, unable to connect to database. Access denied.");
            System.exit(1);
        }
    }

    public static ResultSet executeQuery(String query) throws Exception{
        Statement statement = connection.createStatement();
        try {
            return statement.executeQuery(query); // only return the results if the query has results
        } catch (Exception e){
            return null;
        }
    }

    public static void handleCardSwipe(int id){
        String currentTime = LocalDateTime.now().toString().substring(0,19);
        String date = currentTime.substring(0, 10);
        String time = currentTime.substring(11);

        int entering;
        if (isEntering(id)){
            entering = 1;
            System.out.println("Access granted. Door opened.");
        } else {
            entering = 0;
            System.out.println("Door opened.");
        }

        String query = "insert into access values ('" + id + "', " + entering + ", '" + date + "', '" + time + "');";
        try{
            executeQuery(query);
        } catch(Exception ignored){}
    }

    public static boolean isEntering(int id){
        String query = "SELECT * FROM access WHERE id = " + Integer.toString(id) + " ORDER BY date, time desc;";
        try {
            ResultSet result = executeQuery(query);
            if (result.next()){
                // if their most recent door interaction was an entrance, they must be exiting now
                return result.getObject(2).toString().equals("1");
            } else { // if there are no records, it's their first access, and it'll be an entrance
                return true;
            }
        } catch (Exception ignored){}
        return false;
    }

    public static boolean doesIDExist(int id){
        String query = "SELECT * FROM users WHERE id = " + Integer.toString(id) + ";";
        try {
            ResultSet result = executeQuery(query);
            if (result.next()){
                return true;
            }
        } catch (Exception ignored){}
        return false;
    }

    public static boolean hasAccess(int id){
        String query = "SELECT * FROM users WHERE id = " + Integer.toString(id) + ";";
        try {
            ResultSet result = executeQuery(query);
            if (result.getObject(3).toString().equals("1")){
                return true;
            }
        } catch (Exception ignored){}
        return false;
    }

    public void start(Stage stage) throws Exception{
        LoginController loginControl = new LoginController(stage, this);
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("Login Layout.fxml"));
        loginLoader.setController(loginControl);
        Parent root = loginLoader.load();
        Scene scene = new Scene(root, 640, 374);
        stage.setScene(scene);
        stage.show();
    }

    public void goToQueryScreen(Stage stage) throws Exception{
        QueryScreenController queryControl = new QueryScreenController(stage, this);
        FXMLLoader queryLoader = new FXMLLoader(getClass().getResource("Query Layout.fxml"));
        queryLoader.setController(queryControl);
        Parent root = queryLoader.load();
        Scene scene = new Scene(root, 640, 374);
        stage.setScene(scene);
        stage.show();
    }

    public void showManageUsersPanel(Stage stage) throws Exception{
        ManageUserController manageControl = new ManageUserController(stage, this);
        FXMLLoader manageUserLoader = new FXMLLoader(getClass().getResource("Manage Users Layout.fxml"));
        manageUserLoader.setController(manageControl);
        Parent root = manageUserLoader.load();
        Scene scene = new Scene(root, 640, 374);
        stage.setScene(scene);
        stage.show();
    }

    public static int promptID(){
        System.out.print("\nSwipe ID card:");
        Scanner input = new Scanner(System.in);
        String line = input.nextLine().trim();
        input.close();

        return parseID(line);
    }

    public static int parseID(String text){
        try {
            int id = Integer.parseInt(text.substring(2, 11));
            if (Integer.toString(id).length() != 9){ throw new Exception(); }
            return id;
        } catch(Exception e){
            System.out.println("Invalid ID!");
            return 0;
        }
    }

    public static int getChoice(int min, int max){
        int choice;
        Scanner input = new Scanner(System.in);
        while(true){
            System.out.print("Your choice? (" + min + " - " + max + "): ");

            try {
                choice = Integer.parseInt(input.nextLine());
                if(choice >= min && choice <= max){ return choice;}
                System.out.println("Invalid choice; input must be from " + min + " to " + max + ".");
            } catch (java.util.InputMismatchException e){
                System.out.println("You must input an integer.");
            }
        }
    }
}