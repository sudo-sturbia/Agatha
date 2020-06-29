package com.github.sudo_sturbia.agatha.client.controller;

import com.github.sudo_sturbia.agatha.client.model.Communicator;
import com.github.sudo_sturbia.agatha.client.model.Library;
import com.github.sudo_sturbia.agatha.client.model.ServerInfo;
import com.github.sudo_sturbia.agatha.client.view.View;
import com.github.sudo_sturbia.agatha.core.ExecutionState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;

/** Controller of the login display. */
public class LoginController
{
    /** Pane using the controller. */
    @FXML
    private GridPane loginPane;

    /** A choice box for either sign up or login. */
    @FXML
    private ChoiceBox<String> loginOrSignUp;

    /** Username field. */
    @FXML
    private TextField usernameField;

    /** Password field. */
    @FXML
    private PasswordField passwordField;

    /** A label to write an error message in if sign up/login fails. */
    @FXML
    private Label errorContainer;

    /**
     * A handler for the start button. Based on the choice box,
     * either attempts to login as a client or creates a new account,
     * then logs in as that account.
     *
     * @param actionEvent The event causing this method to be called.
     */
    @FXML
    private void loginOrSignUp(ActionEvent actionEvent)
    {
        if (this.loginOrSignUp.getValue().equals("Login"))
        {
            this.login();
        }
        else
        {
            this.signUp();
        }
    }

    /**
     * Login as a user.
     */
    private void login()
    {
        Library library = Library.getLibrary(usernameField.getText(), passwordField.getText(),
                ServerInfo.getPort(), ServerInfo.getHost());

        if (library != null)
        {
            MainController controller = new MainController(library);

            try
            {
                FXMLLoader loader = new FXMLLoader(View.class.getResource("layouts/mainLayout.fxml"));
                loader.setController(controller);

                GridPane base = ((GridPane) this.loginPane.getParent());
                base.getChildren().remove(this.loginPane);
                base.getChildren().add(loader.load());

                controller.initComponents();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            this.errorContainer.setText("Wrong Credentials.");
        }
    }

    /**
     * Sign up a new user then login as the user.
     */
    private void signUp()
    {
        Communicator communicator = new Communicator(usernameField.getText(), passwordField.getText(),
                ServerInfo.getPort(), ServerInfo.getHost());

        ExecutionState state = communicator.request(ExecutionState.class, Communicator.FUNCTION.CREATE, "");
        if (state != null && state.getCode() == 0)
        {
            this.login();
        }
        else
        {
            this.errorContainer.setText("Sign up failed.");
        }
    }
}
