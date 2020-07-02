package com.github.sudo_sturbia.agatha.client.controller;

import com.github.sudo_sturbia.agatha.client.model.Library;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class LabelController
{
    /** Client's library. */
    private final Library library;

    /** Name of the label. */
    private final String label;

    /** A list of books to display in the tab. */
    private final List<String> bookList;

    /** Controller of the main pane. */
    private final MainController mainController;

    /** Label containing the title. */
    @FXML
    private Label titleLabel;

    /** Delete all books with label button. */
    @FXML
    private Button deleteBooks;

    /** Delete the label button. */
    @FXML
    private Button deleteLabel;

    /** A label's search result (list of books). */
    @FXML
    private VBox labelSearchResult;

    public LabelController(Library library, String label,
                           List<String> bookList, MainController mainController)
    {
        this.library = library;
        this.label = label;
        this.bookList = bookList;
        this.mainController = mainController;
    }

    /** Initialize components of the tab. */
    public void initComponents()
    {
        this.titleLabel.setText(this.label);
        for (String book : this.bookList)
        {
            this.labelSearchResult.getChildren().add(new Label(book));
        }

        this.deleteBooks.setOnAction(event -> LabelController.this.deleteBooks());
        this.deleteLabel.setOnAction(event -> LabelController.this.deleteLabel());
    }

    /** Delete all books in the label. */
    private void deleteBooks()
    {
        this.library.deleteBooksWithLabel(this.label);
        this.mainController.bookList();
    }

    /** Delete the label. */
    private void deleteLabel()
    {
        this.library.deleteLabel(this.label);
    }
}
