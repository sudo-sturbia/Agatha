package com.github.sudo_sturbia.agatha.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/** Controller of the main layout. */
public class MainController
{
    /** All tabs contained in main panel. */
    @FXML
    private TabPane appTabs;

    /** Client's username label. */
    @FXML
    private Label username;

    /** Client's number of books label. */
    @FXML
    private Label numberOfBooks;

    /** User vector. */
    @FXML
    private Label userVector;

    /** Search for a label or a book field. */
    @FXML
    private TextField searchField;

    /** A list of user's books. */
    @FXML
    private VBox booksList;

    /** Name of new book to add. */
    @FXML
    private TextField newBookName;

    /** Author of new book to add. */
    @FXML
    private TextField newBookAuthor;

    /** Number of pages of new book to add. */
    @FXML
    private TextField newBookPages;

    /** Name of new book to add. */
    @FXML
    private ChoiceBox<String> newBookState;

    /** Button to choose cover path of new book to add. */
    @FXML
    private Button coverPathButton;

    /** Display cover image of new book to add. */
    @FXML
    private ScrollPane coverScrollPane;

    /** Button pressed to add a new book. */
    @FXML
    private Button newBookButton;
}
