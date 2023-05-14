module com.example.newgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.newgui to javafx.fxml;
    exports com.example.newgui;
}