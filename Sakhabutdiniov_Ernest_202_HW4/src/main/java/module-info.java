module com.ersakhabutdinov.jigsaw {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.ersakhabutdinov.jigsaw to javafx.fxml;
    exports com.ersakhabutdinov.jigsaw;
}