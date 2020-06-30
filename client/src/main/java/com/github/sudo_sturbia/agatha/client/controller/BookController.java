package com.github.sudo_sturbia.agatha.client.controller;

import com.github.sudo_sturbia.agatha.client.model.Library;
import com.github.sudo_sturbia.agatha.core.Book;
import com.github.sudo_sturbia.agatha.core.BookState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

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

    /** Image view to display cover image. */
    @FXML
    private ImageView coverImage;

    /** Create a BookController with given fields. */
    public BookController(Library library, Book book)
    {
        this.library = library;
        this.book = book;
    }

    /** Initialize components of the tab using the given book. */
    public void initComponents()
    {
        this.nameField.setText(this.book.getName());
        this.authorField.setText(this.book.getAuthor());
        this.pagesField.setText(String.valueOf(this.book.getNumberOfPages()));
        this.readPagesField.setText(String.valueOf(this.book.getNumberOfReadPages()));
        this.stateField.setText(this.book.getStateToString());

        try
        {
            coverImage.setImage(new Image(new FileInputStream(this.book.getCoverImagePath())));
        }
        catch (FileNotFoundException | NullPointerException e)
        {
            // Leave pane empty
            System.err.println(e.getMessage());
        }

        this.updateName.setOnAction(event -> BookController.this.updateName());
        this.updateAuthor.setOnAction(event -> BookController.this.updateAuthor());
        this.updatePages.setOnAction(event -> BookController.this.updatePages());
        this.updateReadPages.setOnAction(event -> BookController.this.updateReadPages());
        this.updateState.setOnAction(event -> BookController.this.updateState());
    }

    /** Update book's name. */
    private void updateName()
    {
    }

    /** Update book's author. */
    private void updateAuthor()
    {
        StackPane pane = (StackPane) this.authorField.getParent();

        TextField field = new TextField("new author");
        field.setId("info-box");
        field.setOnAction(event -> {
            BookController.this.authorField.setText(field.getText());
            BookController.this.book.setAuthor(field.getText());
            BookController.this.library.updateBook(book);

            pane.getChildren().remove(field);
            pane.getChildren().add(BookController.this.authorField);
        });

        pane.getChildren().remove(BookController.this.authorField);
        pane.getChildren().add(field);
    }

    /** Update book's number of pages. */
    private void updatePages()
    {
    }

    /** Update book's read pages. */
    private void updateReadPages()
    {
        StackPane pane = (StackPane) this.readPagesField.getParent();

        TextField field = new TextField("new number");
        field.setId("info-box");
        field.setOnAction(event -> {
            try
            {
                BookController.this.readPagesField.setText(field.getText());
                BookController.this.book.setNumberOfReadPages(Integer.parseInt(field.getText()));
                BookController.this.stateField.setText(BookController.this.book.getStateToString());
                BookController.this.library.updateBook(book);

                pane.getChildren().remove(field);
                pane.getChildren().add(BookController.this.readPagesField);
            }
            catch (NumberFormatException e)
            {
                System.err.println("Read pages is not a number.");
            }
        });

        pane.getChildren().remove(BookController.this.readPagesField);
        pane.getChildren().add(field);
    }

    /** Update book's state. */
    private void updateState()
    {
        StackPane pane = (StackPane) this.stateField.getParent();

        ChoiceBox<String> field = new ChoiceBox<>(FXCollections.observableArrayList(
                "Interested", "Currently Reading", "Read"));
        field.setId("info-box");

        field.setOnAction(event -> {
            BookController.this.stateField.setText(field.getValue());
            BookController.this.book.setState(field.getValue().equals("Interested") ?
                    BookState.State.INTERESTED : field.getValue().equals("Currently Reading") ?
                    BookState.State.CURRENTLY_READING :
                    BookState.State.READ);
            BookController.this.library.updateBook(book);

            pane.getChildren().remove(field);
            pane.getChildren().add(BookController.this.stateField);
        });

        pane.getChildren().remove(BookController.this.stateField);
        pane.getChildren().add(field);
    }
}
