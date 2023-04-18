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
import javafx.scene.text.Text;
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
    VBox leftVBox;
    TextArea txtArea;
    Scene scene;
    Button btnCharger;
    TextField nameField;
    TextField nomFamilleT;

    TextField emailField;

    TextField matriculeT;






    /**
     * La classe main permet de "rouler" le code
     * @param args L'argument est écrit par défaut, la méthode main ne prend pas d'argument
     */
    public static void main(String[] args)  {
        launch(args);
    }

    /**
     *La classe start décrit l'interface javaFx
     * @param primaryStage Le paramètre stage est implanté par défaut dans javaFX
     * @throws IOException L'exception est là pour attraper les cas où l'input est nul
     * @throws ClassNotFoundException lance une exception si la méthode ne peut trouver la méthode processCommandLine ou
     *      * alertHandlers
     */
    @Override
        public void start(Stage primaryStage) throws IOException, ClassNotFoundException {
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

    /**
     * La classe ajout texte est la pour définir l'interface usager, avec ses caractéristiques
     * @param box1 Le paramètre box1 décrit la scene à droite avec toutes les ajouts pour la personnaliser
     */

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

    /**
     * La classe saisonSelection décrit le coté gauche de la scene ,avec le choix de semestre
     * @param box2 Le paramètre box2 est le coté gauche de la scene, vu qu'il est différent du coté droit et qu'ils n'ont pas les mêmes fonctionnalités
     * @throws IOException L'exception est là pour attraper les cas où l'input est nul
     * @throws ClassNotFoundException lance une exception si la méthode ne peut trouver la méthode processCommandLine ou
     *      *      * alertHandlers
     */
    public void saisonSelection(VBox box2) throws IOException, ClassNotFoundException {

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

        btnCharger.setOnAction((event)-> {
            try {
                handle("CHARGER", comboBox.getValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }
    /**
     *
     * @param actionEvent action que l'usager fait en cliquant sur le bouton charger/inscrire
     * @param choiceSession Le paramètre choiceSession est la session qui a été choisie
     * @throws IOException L'exception est là pour attraper les cas où l'input est nul
     * @throws ClassNotFoundException lance une exception si la méthode ne peut trouver la méthode processCommandLine ou
     *      *      * alertHandlers
     */


    public void handle(String actionEvent, String choiceSession) throws IOException, ClassNotFoundException {
        Socket clientFxSocket = new Socket("127.0.0.1", 1337);

        ObjectOutputStream oos = new ObjectOutputStream(clientFxSocket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(clientFxSocket.getInputStream());

        if (actionEvent == "CHARGER") {

            String commandToSend = "CHARGER " + choiceSession;
            oos.writeObject(commandToSend);
            oos.flush();

            ArrayList<server.models.Course> coursesObject = (ArrayList<Course>) ois.readObject();

            ArrayList<String> lineCourses = new ArrayList<>();
            coursesObject.forEach((courses)->System.out.println(courses.getName()));

            //Scene scene = btnCharger.getScene();
            //VBox leftVBox = (VBox) scene.lookup("#leftVBox");

            coursesObject.forEach((courses)-> lineCourses.add(courses.getName() + " " + courses.getCode() + "/n"));
            lineCourses.forEach((stringCourses)-> leftVBox.getChildren().add(new Text(stringCourses)));

        } else if (actionEvent == "INSCRIRE") {

            oos.writeObject("INSCRIRE");
            oos.flush();

            String prenom = nameField.getText();
            String email = emailField.getText();
            String nom = nomFamilleT.getText();
            String matricule = matriculeT.getText();

            String newRegistrationForm = prenom + " " + nom + " " + email +
                    " " + matricule + " " + nom + " " + courseRegistrationCode + " " +
                    courseRegistrationSession;


//Comme nous n'avons pas pu afficher l'information du serveur que nous avons importée
// nous ne pouvons pas envoyer le choix de cours choisi par l'usager, mais c'est comme
// cela qu'on aurait procédé si on avait pu, en transfomant l'information en une string
// comme dans la tâche 2.


            oos.writeObject(newRegistrationForm);
            oos.flush();
        }
    }
}


//Lorsqu'on essaie de rouler l'application, on obtient le code d'erreur "Cannot invoke "javafx.scene.control.Button.getScene()" because "this.btnCharger" is null" pour le bouton
// charger mais aussi pour la VBox. On pense que cela vient du fait que nos méthodes sont séparées et ne sont pas dans
// sous la méthode main, c'est pour ça que les variables sont dites "vides".

