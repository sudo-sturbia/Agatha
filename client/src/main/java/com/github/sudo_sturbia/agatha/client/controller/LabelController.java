package com.github.sudo_sturbia.agatha.client.controller;

import com.github.sudo_sturbia.agatha.client.model.Library;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.util.List;

public class LabelController
{
    /** Client's library. */
    private final Library library;

    /** A list of books to display in the tab. */
    private final List<String> bookList;

    /** A label's search result (list of books). */
    @FXML
    private VBox labelSearchResult;

    public LabelController(Library library, List<String> bookList)
    {
        this.library = library;
        this.bookList = bookList;
    }

    /** Initialize components of the tab. */
    public void initComponents()
    {
    }
}
