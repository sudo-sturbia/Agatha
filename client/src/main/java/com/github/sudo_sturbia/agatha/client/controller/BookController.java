package com.github.sudo_sturbia.agatha.client.controller;

import com.github.sudo_sturbia.agatha.client.model.Library;
import com.github.sudo_sturbia.agatha.core.Book;
import com.github.sudo_sturbia.agatha.core.BookState;
import com.github.sudo_sturbia.agatha.core.ExecutionState;
import com.github.sudo_sturbia.agatha.core.Note;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/** Controller of the book display. */
public class BookController
{
    /** Client's library. */
    private final Library library;

    /** Book controlled by this controller. */
    private final Book book;

    /** Tab Pane containing the book tab. */
    private final TabPane tabs;

    /** Controller of the main pane. */
    private final MainController mainController;

    @FXML
    private Tab tab;

    /** Add a label to the book. */
    @FXML
    private TextField addLabelField;

    /** Remove a label from the book. */
    @FXML
    private TextField removeLabelField;

    /** Delete the book. */
    @FXML
    private Button deleteButton;

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

    /** Pane to contain both notes and a cover. */
    @FXML
    private ScrollPane coverNotePane;

    /** Page number to display a note. */
    @FXML
    private ChoiceBox<String> notePage;

    /** Label to display error messages. */
    @FXML
    private Label errorBox;

    /** Create a BookController with given fields. */
    public BookController(Library library, Book book, TabPane tabs, MainController mainController)
    {
        this.tabs = tabs;
        this.library = library;
        this.book = book;
        this.mainController = mainController;
    }

    /** Initialize components of the tab using the given book. */
    public void initComponents()
    {
        this.nameField.setText(this.book.getName());
        this.authorField.setText(this.book.getAuthor());
        this.pagesField.setText(String.valueOf(this.book.getNumberOfPages()));
        this.readPagesField.setText(String.valueOf(this.book.getNumberOfReadPages()));
        this.stateField.setText(this.book.getStateToString());
        this.displayCover();

        // Fill pages choice box
        List<String> pages = new ArrayList<>();
        pages.add("cover");
        for (int i = 0, size = this.book.getNumberOfPages(); i < size; i++)
        {
            pages.add(i + "");
        }

        this.notePage.setItems(FXCollections.observableList(pages));
        this.notePage.setValue("cover");

        this.deleteButton.setOnAction(event -> BookController.this.delete());
        this.addLabelField.setOnAction(event -> BookController.this.addLabel());
        this.removeLabelField.setOnAction(event -> BookController.this.removeLabel());
        this.updateName.setOnAction(event -> BookController.this.updateName());
        this.updateAuthor.setOnAction(event -> BookController.this.updateAuthor());
        this.updatePages.setOnAction(event -> BookController.this.updatePages());
        this.updateReadPages.setOnAction(event -> BookController.this.updateReadPages());
        this.updateState.setOnAction(event -> BookController.this.updateState());
        this.notePage.setOnAction(event -> BookController.this.handleNote());
    }

    /** Delete the controlled book. */
    private void delete()
    {
        this.library.deleteBookWithName(this.book.getName());
        this.tabs.getTabs().remove(this.tab);
        this.mainController.bookList();
        this.errorBox.setText("");
    }

    /** Add a label to the book. */
    private void addLabel()
    {
        ExecutionState state = this.library.addLabelToBook(this.book.getName(), this.addLabelField.getText());
        if (state == null || state.getCode() != 0)
        {
            this.errorBox.setText("Operation failed.");
        }
        else
        {
            this.errorBox.setText("");
        }
    }

    /** Remove a label from the book. */
    private void removeLabel()
    {
        ExecutionState state = this.library.deleteLabelFromBook(this.book.getName(), this.removeLabelField.getText());
        if (state == null || state.getCode() != 0)
        {
            this.errorBox.setText("Operation failed.");
        }
        else
        {
            this.errorBox.setText("");
        }
    }

    /** Search for a note and create a noteTab if found. */
    private void handleNote()
    {
        if (this.notePage.getValue().equals("cover"))
        {
            this.displayCover();
        }
        else
        {
            // Display a note or an option to create a note
            int page = Integer.parseInt(this.notePage.getValue());
            if (this.book.getNoteAtPage(page) != null)
            {
                this.displayNote(page);
            }
            else
            {
                this.createNote(page);
            }
       }

        this.errorBox.setText("");
    }

    /** Display a note with an option to change. */
    private void displayNote(int page)
    {
        Note note = book.getNoteAtPage(page);

        // Create a note label that changes to a field when
        // mouse is clicked to allow user to update the note.
        StackPane notePane = new StackPane();
        Label label = new Label(note.getNote());
        label.setId("note-text");
        label.setWrapText(true);

        label.setOnMouseClicked(event -> {
            notePane.getChildren().remove(label);

            TextField field = new TextField(label.getText());
            field.setId("note-field-text");
            field.setAlignment(Pos.CENTER);

            notePane.getChildren().add(field);

            field.setOnAction(event1 -> {
                BookController.this.book.getNoteAtPage(page).setNote(field.getText());
                BookController.this.library.updateBook(book);

                label.setText(field.getText());
                notePane.getChildren().remove(field);
                notePane.getChildren().add(label);
            });
        });

        notePane.getChildren().add(label);
        this.coverNotePane.setContent(notePane);
    }

    /** Display a text field to create a note. */
    private void createNote(int page)
    {
        StackPane notePane = new StackPane();
        TextField field = new TextField("note");
        field.setId("note-field-text");
        field.setAlignment(Pos.CENTER);

        field.setOnAction(event -> {
            BookController.this.book.addNote(field.getText(), page);
            BookController.this.library.updateBook(book);

            notePane.getChildren().remove(field);

            Label label = new Label(field.getText());
            label.setId("note-text");
            label.setWrapText(true);

            notePane.getChildren().add(label);
        });

        notePane.getChildren().add(field);
        this.coverNotePane.setContent(notePane);
    }

    /** Update book's name. */
    private void updateName()
    {
        this.errorBox.setText("Book's name can't be updated.");
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
        this.errorBox.setText("");
    }

    /** Update book's number of pages. */
    private void updatePages()
    {
        this.errorBox.setText("Number of pages can't be updated.");
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
        this.errorBox.setText("");
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
            BookController.this.readPagesField.setText(String.valueOf(BookController.this.book.getNumberOfReadPages()));
            BookController.this.library.updateBook(book);

            pane.getChildren().remove(field);
            pane.getChildren().add(BookController.this.stateField);
        });

        pane.getChildren().remove(BookController.this.stateField);
        pane.getChildren().add(field);
        this.errorBox.setText("");
    }

    /** Display book's cover image. */
    private void displayCover()
    {
        try
        {
            this.coverNotePane.setContent(new ImageView(new Image(new FileInputStream(this.book.getCoverImagePath()))));
        }
        catch (FileNotFoundException | NullPointerException e)
        {
            this.coverNotePane.setContent(null);
            System.err.println("No cover image.");
        }

        this.errorBox.setText("");
    }
}
