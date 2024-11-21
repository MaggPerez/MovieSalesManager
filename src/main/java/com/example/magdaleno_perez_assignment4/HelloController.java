package com.example.magdaleno_perez_assignment4;
//I certify that this submission is my original work - Magdaleno Perez
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class HelloController {
    //*****************************
    // FX:id TableView and Columns
    //*****************************
    @FXML
    private TableView<Movie> movieTableView;

    @FXML
    private TableColumn<Movie, String> tableColumnTitle;

    @FXML
    private TableColumn<Movie, Integer> tableColumnYear;

    @FXML
    private TableColumn<Movie, Double> tableColumnSales;



    //*****************************
    // FX:id TextFields and Labels
    //*****************************

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField yearTextField;

    @FXML
    private TextField salesTextField;


    @FXML
    private Label statusLabel;


    //**********************************************************
    // Collections and Interfaces to import and export JSON File
    //**********************************************************
    Collection<Movie> list = new ArrayList<>(); //Used to import JSON data


    ArrayList<Movie> JSONArray = new ArrayList<>(); //Used to export JSON Data







    //***********************************************************************************************************
    //                                  Initializing Table Columns
    //***********************************************************************************************************
    public void initialize(){
        tableColumnTitle.setCellValueFactory(new PropertyValueFactory<Movie, String>("Title"));
        tableColumnYear.setCellValueFactory(new PropertyValueFactory<Movie, Integer>("Year"));
        tableColumnSales.setCellValueFactory(new PropertyValueFactory<Movie, Double>("Sales"));
    }









    //***********************************************************************************************************
    //                                  Creating MS Access Table Event Handler
    //***********************************************************************************************************
    public void onHandleCreateMSAccessTable(){
        statusLabel.setText("New Table Created");
        ObservableList<Movie> movieList = movieTableView.getItems();
        //****************************************
        //Creating database
        //****************************************

        String dbFilePath = ".//MovieDB.accdb";
        String databaseURL = "jdbc:ucanaccess://" + dbFilePath;
        File dbFile = new File(dbFilePath);
        if (!dbFile.exists()) {
            try (Database db =
                         DatabaseBuilder.create(Database.FileFormat.V2010, new File(dbFilePath))) {
                System.out.println("The database file has been created.");
            } catch (IOException ioe) {
                ioe.printStackTrace(System.err);
            }
        }

        //****************************************
        //Dropping Table if "Create Table" is selected
        //****************************************
        else{
            movieList.clear();
            try {
                System.out.println("New Table Created");
                Connection conn = DriverManager.getConnection(databaseURL);
                String sql;
                sql = "DROP TABLE MovieDB";
                Statement createTableStatement = conn.createStatement();
                createTableStatement.execute(sql);
                conn.commit();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }



        //****************************************
        //Creating table
        //****************************************
        try {
            Connection conn = DriverManager.getConnection(databaseURL);
            String sql;
            sql = "CREATE TABLE MovieDB (Title nvarchar(255), Year INT, Sales DOUBLE)";
            Statement createTableStatement = conn.createStatement();
            createTableStatement.execute(sql);
            conn.commit();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }


    }       //end of onHandleCreateMSAccessTable() method









    //***********************************************************************************************************
    //                                  Importing JSON File from User
    //***********************************************************************************************************

    /**
     * When importJSONFile Event Handler is clicked:
     * - The program will take user's current directory containing project files and a window will be shown for the user
     * to select JSON files from the folder.
     *
     * - Once a file is selected, a method will take in the file and extract the data (ex. marvel.json) into an ArrayList
     */
    public void importJSONFile(){
        Stage stage = new Stage();
        stage.setTitle("Open");


        //************************************************************************************************
        //Creating a window to open the "Magdaleno_Perez_Assignment4" folder for user to select JSON files.
        //************************************************************************************************


        try{
            //Grabbing the current directory path of the computer which will lead to "Magdaleno_Perez_Assignment4"
            String currectDirectory = System.getProperty("user.dir");


            //fileChooser will help us open the "Magdaleno_Perez_Assignment4"
            FileChooser fileChooser = new FileChooser();


            //InitialDirectory will store the User's currentDirectory Path to be able to open
            // "Magdaleno_Perez_Assignment4" folder
            File initialDirectory = new File(currectDirectory);


            //fileChooser will be set to open this folder containing Assignment 4 project files
            fileChooser.setInitialDirectory(initialDirectory);



            //Setting a filter so that only JSON files can be shown and to hide non JSON files.
            FileChooser.ExtensionFilter extensionFilter =
                    new FileChooser.ExtensionFilter("JSON files(*.json)", "*.json");
            fileChooser.getExtensionFilters().add(extensionFilter);


            //When "Import JSON" menu item is clicked,
            //showOpenDialog will open the Window containing Assignment 4 Project Files
            File usersJsonFile = fileChooser.showOpenDialog(stage);



            //If user doesn't choose a file, a message is shown on console to choose a JSON File
            if(usersJsonFile == null){
                System.out.println("Choose a JSON file.");
            }
            else{
                //If user imports JSON File, it will extract JSON Files, add them into an ArrayList
                //and print them onto TableView, and then store them into the MS Access DB
                extractJSONData(usersJsonFile);
            }

        }           //End of Try
        catch (Exception e){
            e.printStackTrace();
        }
    }









    //***********************************************************************************************************
    //                                  Extract JSON Data Method
    //***********************************************************************************************************

    /**
     * In this extractJSONData method:
     * - It reads the JSON file selected (ex. marvel.json) and temporarily puts the data into a Collection interface
     * called "list".
     *
     * - Once "list" extracts the data, we'll add the data into the arraylist called JSONArray
     *
     * - Finally addDataToDatabase method will take in the JSONArray and store its data into the MS Access DB
     */
    public void extractJSONData(File file){
        //Updating status label that JSON file was imported from User's current directory
        statusLabel.setText("Import data from " + file);

        ObservableList<Movie> movieList = movieTableView.getItems();

        //Prevents duplication
        JSONArray.clear();

        try{
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            //Reads the JSON File that the User selected.
            FileReader fr = new FileReader(file);


            //Extracting the data and temporarily putting them into the Collection<Grade> list interface.
            list = gson.fromJson(fr, new TypeToken<ArrayList<Movie>>(){}.getType());


            //Dumping JSON Data into arrayList
            for(Movie jsonMovieData: list){
                JSONArray.add(jsonMovieData);
            }

            Iterator<Movie> iter = JSONArray.iterator();

            //Displaying JSON data into TableView
            while(iter.hasNext()){
                movieList.add(iter.next());
            }


            //Once JSON Data is displayed on Table View, the data will be inserted into the Database
            addDataToDatabase(JSONArray);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }









    //***********************************************************************************************************
    //                                  Exit Menu Event Handler
    //***********************************************************************************************************

    /**
     * Event Handler that closes the program
     */
    public void onHandleExitMenu(){
        Platform.exit();
    }









    //***********************************************************************************************************
    //                                  Help | About Dialog Menu Event Handler
    //***********************************************************************************************************

    /**
     * Academic Integrity Pop up Window
     */
    public void onHandleHelpAboutDialog(){
        statusLabel.setText("About Dialog Clicked.");
        //Creating Alert Window
        Alert showAlert = new Alert(Alert.AlertType.NONE);
        showAlert.setAlertType(Alert.AlertType.INFORMATION);


        //Setting Title
        showAlert.setTitle("About Movie Database");


        //Setting Header Text with Name and Integrity Statement
        showAlert.setHeaderText("Name and Integrity Statement");


        //Setting my name and full Integrity Statement stating that the submission is my original work
        showAlert.setContentText("Magdaleno Perez " +
                "\n\nI certify that this submission is my original work.");

        //Shows popup window.
        showAlert.show();
    }









    //***********************************************************************************************************
    //                                  On Handle Export JSON Event Handler
    //***********************************************************************************************************

    /**
     * When onHandleExportJSON Event Handler is clicked:
     * - The program will take user's current directory containing project files and a window will be shown for the user
     * to save a JSON file, with its appropiate movie data, to the folder.
     *
     * - Then it will run the method exportJSONFunction where we'll grab data from the table view and export them into
     * JSON file
     *
     */
    public void onHandleExportJSON(){

        Stage stage = new Stage();
        stage.setTitle("Save");



        //************************************************************************************************
        //Creating a window to open the "Magdaleno_Perez_Assignment4" folder for user to select JSON files.
        //************************************************************************************************

        ////Grabbing the current directory path of the computer which will lead to "Magdaleno_Perez_Assignment4"
        String currectDirectory = System.getProperty("user.dir");


        //fileChooser will help us open the "Magdaleno_Perez_Assignment4"
        FileChooser fileChooser = new FileChooser();


        //InitialDirectory will store the User's currentDirectory Path to be able to open
        // "Magdaleno_Perez_Assignment4" folder
        File initialDirectory = new File(currectDirectory);


        //fileChooser will be set to open this folder containing Magdaleno_Perez_Assignment4 project files
        fileChooser.setInitialDirectory(initialDirectory);


        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("JSON files(*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extensionFilter);


        //Adding fileChooser Object and stage to the export JSON Function method.
        exportJSONFunction(fileChooser, stage);

    }









    //***********************************************************************************************************
    //                                  Export JSON File Function
    //***********************************************************************************************************

    /**
     * In this exportJSONFunction method:
     * - We'll take any data displayed in the movie Table View and convert them into a JSON file
     */
    public void exportJSONFunction(FileChooser fileChooser, Stage stage){
        //Prevents duplicated data from JSON
        JSONArray.clear();
        ObservableList<Movie> movieList = movieTableView.getItems();


        //We'll take any data displayed in tableView and insert it into JSONArray
        for(Movie movieData: movieList){
            JSONArray.add(movieData);
        }


        //Opening window to save JSON file
        File saveFile = fileChooser.showSaveDialog(stage);

        GsonBuilder builder = new GsonBuilder();

        //calling setPrettyPrinting() to format JSON file when saving
        Gson gson = builder.setPrettyPrinting().create();

        //Grabbing JSONArray data and writing it to a JSON File
        String jsonString = gson.toJson(JSONArray);
        try {
            PrintStream ps = new PrintStream(saveFile);
            ps.println(jsonString);


            //Setting status label that the file was saved.
            statusLabel.setText("File Exported.");


        } catch (Exception e) {
            System.out.println("Saving Canceled");
        }
    }









    //***********************************************************************************************************
    //                                      List Records Event Handler
    //***********************************************************************************************************

    /**
     * When List Record Event Handler is clicked:
     * - It will read the MS Access database and display every data to the Table View
     */
    public void onHandleListRecords(){
        statusLabel.setText("Movie table displayed");
        ObservableList<Movie> movieList = movieTableView.getItems();
        movieList.clear();

        try {
            //****************************************
            //Displaying data from MS Access to TableView
            //****************************************
            String dbFilePath = ".//MovieDB.accdb";
            String databaseURL = "jdbc:ucanaccess://" + dbFilePath;
            Connection conn = DriverManager.getConnection(databaseURL);

            String tableName = "MovieDB";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("select * from " + tableName);


            while (result.next()) {
                String title = result.getString("Title");
                int year = result.getInt("Year");
                double sales = result.getDouble("Sales");
                Movie movieData = new Movie(title, year, sales);

                //Adding all movie data to the table view
                movieList.add(movieData);
            }

        } catch (SQLException except) {
            except.printStackTrace();
        }
    }









    //***********************************************************************************************************
    //                                  Adding JSON Data to Database Method
    //***********************************************************************************************************

    /**
     * In this addDataToDatabase method:
     * - The method will take in the JSONArray arrayList containing the extracted JSON file
     * and add them into the MS Access Database
     */
    public void addDataToDatabase(ArrayList<Movie> movieArrayList){
        try {
            String dbFilePath = ".//MovieDB.accdb";
            String databaseURL = "jdbc:ucanaccess://" + dbFilePath;
            Connection conn = DriverManager.getConnection(databaseURL);

            String sql = "INSERT INTO MovieDB (Title, Year, Sales) VALUES (?, ?, ?)";

            PreparedStatement preparedStatement = null;

            //Adding JSON Data to the MS Access DB
            for(Movie movieData: movieArrayList){
                try {
                    preparedStatement = conn.prepareStatement(sql);
                    preparedStatement.setString(1, movieData.getTitle());
                    preparedStatement.setInt(2, movieData.getYear());
                    preparedStatement.setDouble(3, movieData.getSales());
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            System.out.println("JSON Data inserted into Database");

        }       //end of first "try"
        catch (SQLException except){
            except.printStackTrace();
        }
    }









    //***********************************************************************************************************
    //                                  Adding new record Event Handler
    //***********************************************************************************************************

    /**
     * In onHandleAddNewRecords Event Handler
     * - It first takes the 3 inputs from the Title, Year, and Sales TextFields from user.
     * - Next it runs through the validation tests to see if the inputs pass the requirements.
     * - If passed then the new data will be made into a Movie object and added into Table View and JSONArray arraylist.
     * - And finally the new record will be added to the MS Access Database
     */
    public void onHandleAddNewRecords(){
        ObservableList<Movie> movieList = movieTableView.getItems();
        String getTitleText, getYearText, getSalesText;

        //Getting texts for each TextFields
        getTitleText = titleTextField.getText();
        getYearText = yearTextField.getText();
        getSalesText = salesTextField.getText();


        //Validating all tests on its requirements
        Validation.validateTitle(getTitleText);
        Validation.validateYear(getYearText);
        Validation.validateSales(getSalesText);


        /**
         * If all three validations pass, it gets added to TableView and Database
         * Basically if all validations return " ", this means all validations passed
         */
        if(Validation.titleMessage.equals(" ") && Validation.yearMessage.equals(" ") && Validation.salesMessage.equals(" ")){
            System.out.println("All Validations passed");


            //**********************************************************
            // Adding new record to table view and database.
            //**********************************************************

            //Putting new data into Movie class
            Movie newAddedRecord = new Movie(getTitleText, Integer.parseInt(getYearText), Double.parseDouble(getSalesText));

            //Adding new movie record to Table View and JSONArray
            movieList.add(newAddedRecord);
            JSONArray.add(newAddedRecord);

            //Adding new record to MS Access Database
            try {
                String dbFilePath = ".//MovieDB.accdb";
                String databaseURL = "jdbc:ucanaccess://" + dbFilePath;
                Connection conn = DriverManager.getConnection(databaseURL);

                String sql = "INSERT INTO MovieDB (Title, Year, Sales) VALUES (?, ?, ?)";

                PreparedStatement preparedStatement = null;
                    try {
                        preparedStatement = conn.prepareStatement(sql);
                        preparedStatement.setString(1, newAddedRecord.getTitle());
                        preparedStatement.setInt(2, newAddedRecord.getYear());
                        preparedStatement.setDouble(3, newAddedRecord.getSales());
                        preparedStatement.executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                //Clears TextFields
                clearTextFields(titleTextField, yearTextField, salesTextField);

                //Setting the status label that a movie was inserted
                statusLabel.setText("A movie has been inserted: " + newAddedRecord.getTitle());


            }       //end of first "try"
            catch (SQLException except){
                except.printStackTrace();
            }

        }               //end of first "if" statement
        else{
            /**
             * If any of the validations fail or all of them failed, an alert window will appear according
             * to the error the user made.
             * - Shows error message to TextFields being empty
             * - Shows error message of Invalid Inputs
             */
            Alert showAlert = new Alert(Alert.AlertType.NONE);

            //An alert message appears if TextFields are empty or the inputs are invalid.
            String warningMessage = Validation.titleMessage + "\n" + Validation.yearMessage +
                    "\n" + Validation.salesMessage;

            showAlert.setAlertType(Alert.AlertType.WARNING);
            showAlert.setTitle("Invalid Input");
            showAlert.setContentText(warningMessage);
            showAlert.show();
        }


    }








    //***********************************************************************************************************
    //                                  Clear TextFields Method
    //***********************************************************************************************************

    /**
     * Clears TextFields after the Input validations are correct
     */
    public void clearTextFields(TextField title, TextField year, TextField sales){
        title.clear();
        year.clear();
        sales.clear();
    }








    //***********************************************************************************************************
    //                                  Delete Record Event Handler
    //***********************************************************************************************************

    /**
     * When the Delete Record Event Handler is clicked:
     * - If a row is selected, that selected item will be removed from the Table View and will be passed into the
     * deleteSelectedMovieFromMSAccess method where it will be removed from MS Access Database too.
     */
    public void onHandleDeleteRecord(){
        Movie selectedItem = movieTableView.getSelectionModel().getSelectedItem();

        //If user chooses a row, it will get deleted.
        // Otherwise, console will throw a message to select a record to delete
        if(selectedItem != null){
            movieTableView.getItems().remove(selectedItem);
            JSONArray.remove(selectedItem);
            statusLabel.setText("A movie has been deleted: " + selectedItem.getTitle());
            deleteSelectedMovieFromMSAccess(selectedItem);
        }
        else{
            System.out.println("Select a record to delete");
        }
    }








    //***********************************************************************************************************
    //                          Method that takes in the selected item to be deleted
    //***********************************************************************************************************

    /**
     * Once a selected row is passed into this method, it will be removed from the database.
     */
    public void deleteSelectedMovieFromMSAccess(Movie selectedItem){
        //Title, year, and sales will have the record we want to remove from MS Access
        String title = selectedItem.getTitle();
        int year = selectedItem.getYear();
        double sales = selectedItem.getSales();



        //****************************************
        //Removing selected record from MS Access
        //****************************************
        try{
            String dbFilePath = ".//MovieDB.accdb";
            String databaseURL = "jdbc:ucanaccess://" + dbFilePath;
            Connection conn = DriverManager.getConnection(databaseURL);

            String sql = "DELETE FROM MovieDB WHERE Title=? AND Year=? AND Sales=? ";
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, title);
                preparedStatement.setInt(2, year);
                preparedStatement.setDouble(3, sales);
                int rowsDeleted = preparedStatement.executeUpdate();
            } catch (SQLException e) {
                System.err.println("SQLState: " + e.getSQLState());
                System.err.println("Error Code: " + e.getErrorCode());
                System.err.println("Message: " + e.getMessage());
                e.printStackTrace();

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


}