module com.example.magdaleno_perez_assignment4 {
//    I certify that this submission is my original work - Magdaleno Perez
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires com.healthmarketscience.jackcess;
    requires com.google.gson;


    opens com.example.magdaleno_perez_assignment4 to javafx.fxml, com.google.gson;
    exports com.example.magdaleno_perez_assignment4;
}