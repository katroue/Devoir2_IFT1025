package applicationGui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import server.models.Course;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;
import java.net.Socket;

import java.util.ArrayList;


public class clientFx extends Application {

    public static void main(String[] args) throws IOException {
        Socket clientFx = new Socket("127.0.0.1", 1337);

        ObjectOutputStream oos = new ObjectOutputStream(clientFx.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(clientFx.getInputStream());
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create a label for the left region & Create a VBox to hold the left region content
        Label leftLabel = new Label(" Liste des cours ");
        VBox leftVBox = new VBox(leftLabel);

        TextArea txtA = new TextArea("Tous les cours seront affiches ici :");
        txtA.setPrefSize(400,400);
        leftVBox.getChildren().add(txtA);

        saisonSelection(leftVBox);


        // Create a label for the right region & Create a VBox to hold the right region content
        Label rightLabel = new Label(" Formulaire d'inscription ");
        VBox rightVBox = new VBox(rightLabel);
        ajoutTexte(rightVBox);


        // Create a BorderPane to hold the left and right regions
        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(leftVBox);
        borderPane.setRight(rightVBox);

        // Create a new scene with the BorderPane and set it as the primary stage's scene
        Scene scene = new Scene(borderPane, 650, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void ajoutTexte(VBox box1){
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        // Add labels and input fields for name, email, and password
        Label nameLabel = new Label("Prenom :");
        TextField nameField = new TextField();
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameField, 1, 0);

        Label nomFamilleL = new Label("Nom :");
        TextField nomFamilleT = new TextField();
        gridPane.add(nomFamilleL, 0, 1);
        gridPane.add(nomFamilleT, 1, 1);

        Label emailLabel = new Label(" Email :");
        TextField emailField = new TextField();
        gridPane.add(emailLabel, 0, 2);
        gridPane.add(emailField, 1, 2);

        Label matriculeL = new Label(" Matricule :");
        TextField matriculeT = new TextField();
        gridPane.add(matriculeL, 0, 3);
        gridPane.add(matriculeT, 1, 3);

        // Add a button to submit the form
        Button submitButton = new Button(" Envoyer ");
        gridPane.add(submitButton, 1, 4);

        // vBox.getChildren().add(newPane);
        box1.getChildren().add(gridPane);
    }
    public void saisonSelection(VBox box2){

        ObservableList<String> choices = FXCollections.observableArrayList(
                " Hiver ", " Été ", " Automne ");
        // Create a combo box and set its choices
        ComboBox<String> comboBox = new ComboBox<>(choices);

        // Set the default value of the combo box
        comboBox.setValue("Hiver");

        Button btnCharger = new Button("Charger");
        // add action listening

        HBox sectionCharger = new HBox();

        // Create a VBox container to hold the combo box
        sectionCharger.getChildren().add(comboBox);
        sectionCharger.getChildren().add(btnCharger);

        box2.getChildren().add(sectionCharger);
        // Create a new scene with the VBox and set it as the primary stage's scene
        Scene scene = new Scene(box2, 400, 400);

        //Event handlers

        return btnCharger.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (actionEvent == btnCharger) {
                    Socket clientFx = null;
                    try {
                        clientFx = new Socket("127.0.0.1", 1337);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    ObjectOutputStream oos = null;
                    try {
                        oos = new ObjectOutputStream(clientFx.getOutputStream());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    ObjectInputStream ois = null;
                    try {
                        ois = new ObjectInputStream(clientFx.getInputStream());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    oos.writeObject("CHARGER " + session);
                    try {
                        oos.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    ArrayList<server.models.Course> coursesObject = (ArrayList<server.models.Course>) ois.readObject();
                } else if (actionEvent == "INSCRIRE") {
                    oos.writeObject("INSCRIRE");
                    oos.flush();


                }
            }
        })



                ;
    }
}




