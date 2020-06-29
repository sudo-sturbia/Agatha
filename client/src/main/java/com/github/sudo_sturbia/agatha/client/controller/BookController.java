package com.github.sudo_sturbia.agatha.client.controller;

import com.github.sudo_sturbia.agatha.client.model.Library;
import com.github.sudo_sturbia.agatha.core.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/** Controller of the book display. */
public class BookController
{
    /** Client's library. */
    private final Library library;

    /** Book controlled by this controller. */
    private final Book book;

    /** Update book's name. */
    @FXML
    private Button updateName;

    /** Display book's name. */
    @FXML
    private Label nameField;

    /** Update book's author. */
    @FXML
    private Button updateAuthor;

    /** Display book's author. */
    @FXML
    private Label authorField;

    /** Update book's state. */
    @FXML
    private Button updateState;

    /** Display book's state. */
    @FXML
    private Label stateField;

    /** Update book's number of pages. */
    @FXML
    private Button updatePages;

    /** Display book's number of pages. */
    @FXML
    private Label pagesField;

    /** Update book's number of read pages. */
    @FXML
    private Button updateReadPages;

    /** Display book's number of read pages. */
    @FXML
    private Label readPagesField;

    /** Create a BookController with given fields. */
    public BookController(Library library, Book book)
    {
        this.library = library;
        this.book = book;
    }

    /** Initialize components of the tab using the given book. */
    public void initComponents()
    {
    }
}
