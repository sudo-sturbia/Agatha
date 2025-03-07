package com.github.sudo_sturbia.agatha.client.controller;

import com.github.sudo_sturbia.agatha.client.model.Library;
import com.github.sudo_sturbia.agatha.client.view.View;
import com.github.sudo_sturbia.agatha.core.Book;
import com.github.sudo_sturbia.agatha.core.BookBuilder;
import com.github.sudo_sturbia.agatha.core.BookState;
import com.github.sudo_sturbia.agatha.core.ExecutionState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;

/** Controller of the main layout. */
public class MainController
{
    /** User's library. */
    private final Library library;

    /** All tabs contained in main panel. */
    @FXML
    private TabPane tabs;

    /** Client's username label. */
    @FXML
    private Label username;

    /** Client's number of books label. */
    @FXML
    private Label numberOfBooks;

    /** User vector. */
    @FXML
    private Label userVector;

    /** A list of user's books. */
    @FXML
    private VBox booksList;

    /** Search for a label or a book field. */
    @FXML
    private TextField searchField;

    /** Search or create choice box. */
    @FXML
    private ChoiceBox<String> searchOrCreate;

    /** Label used to display search errors. */
    @FXML
    private Label searchError;

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

    /** Path to book's cover. Specified by the user. */
    private String coverPath;

    /** Pane to display cover image of new book to add. */
    @FXML
    private ScrollPane coverScrollPane;

    /** Button pressed to add a new book. */
    @FXML
    private Button newBookButton;

    /** Label used to display errors when creating a new book. */
    @FXML
    private Label newBookError;

    /**
     * Set client's library.
     *
     * @param library client's library.
     */
    public MainController(Library library)
    {
        this.library = library;
    }

    /**
     * Initialize all components of mainLayout.
     */
    public void initComponents()
    {
        this.userInfo();
        this.bookList();

        this.newBookButton.setOnAction(event -> MainController.this.newBook());
        this.searchField.setOnAction(event -> MainController.this.search());
        this.coverPathButton.setOnAction(event -> MainController.this.findCover());
    }

    /** Set user's book list and number of books. */
    public void bookList()
    {
        this.booksList.getChildren().clear();

        List<String> bookList = this.library.getNamesOfBooks();
        for (String book : bookList)
        {
            this.booksList.getChildren().add(new Label(book));
        }

        this.numberOfBooks.setText(bookList.size() + " Books");
    }

    /** Set user information (username and user's vector.) */
    private void userInfo()
    {
        this.username.setText(library.getUsername());
        this.userVector.setText(String.valueOf(Character.toUpperCase(library.getUsername().charAt(0))));
    }

    /**
     * Add a new book to user's library. This method is called when
     * the user presses "add" button.
     */
    private void newBook()
    {
        try
        {
            Book book = BookBuilder.newBook()
                                   .name(this.newBookName.getText())
                                   .numberOfPages(Integer.parseInt(this.newBookPages.getText()))
                                   .state(this.newBookState.getValue().equals("Interested") ?
                                           BookState.State.INTERESTED : this.newBookState.getValue().equals("Currently Reading") ?
                                           BookState.State.CURRENTLY_READING :
                                           BookState.State.READ)
                                   .author(this.newBookAuthor.getText())
                                   .coverPath(this.coverPath)
                                   .build();

            ExecutionState state = this.library.addBook(book);
            if (state.getCode() == 0)
            {
                this.bookList();
            }
            else
            {
                this.newBookError.setText("Operation failed: each book should have a unique name.");
            }
        }
        catch (NumberFormatException e)
        {
            this.newBookError.setText("Number of pages is not an integer.");
        }
        catch (IllegalArgumentException e)
        {
            this.newBookError.setText(e.getMessage());
        }
    }

    /**
     * Search for a book or a label with the name specified in the search
     * field. If either a book or label (or both) is found a new tab is
     * created. The tab is either a bookTab or a labelTab.
     */
    private void search()
    {
        if (this.searchOrCreate.getValue().equals("create"))
        {
            this.createLabel(this.searchField.getText());
        }
        else
        {
            boolean bookSearch = this.searchForBook(this.searchField.getText());
            boolean labelSearch = this.searchForLabel(this.searchField.getText());
            if (!(bookSearch || labelSearch))
            {
                this.searchError.setText("No search result found.");
            }
            else
            {
                this.searchError.setText("");
            }
        }
    }

    /**
     * Search for a book with the specified name. If book is found
     * creates a new bookTab and fills it with the needed information.
     *
     * @param name name of the book to search for.
     * @return true if a book is found, false otherwise.
     */
    private boolean searchForBook(String name)
    {
        Book book = this.library.getBookWithName(name);
        if (book == null || book.getName() == null)
        {
            return false;
        }

        try
        {
            BookController controller = new BookController(this.library, book, this.tabs, this);
            FXMLLoader loader = new FXMLLoader(View.class.getResource("layouts/bookTab.fxml"));
            loader.setController(controller);

            // Load tab and add a name
            Tab tab = loader.load();
            tab.setText("book:" + book.getName());

            this.tabs.getTabs().add(tab);

            controller.initComponents();
        }
        catch (IOException e)
        {
            // Ignore
        }

        return true;
    }

    /**
     * Search for a label with specified name. If the label is found
     * create a new labelTab and fill it with the needed information.
     *
     * @param name name of the label to search for.
     * @return true if a label is found, false otherwise.
     */
    private boolean searchForLabel(String name)
    {
        List<String> bookList = this.library.getNamesOfBooksWithLabel(name);
        if (bookList == null)
        {
            return false;
        }

        try
        {
            LabelController controller = new LabelController(this.library, name, bookList, this);
            FXMLLoader loader = new FXMLLoader(View.class.getResource("layouts/labelTab.fxml"));
            loader.setController(controller);

            // Load tab and add a name
            Tab tab = loader.load();
            tab.setText("label:" + name);

            this.tabs.getTabs().add(tab);

            controller.initComponents();
        }
        catch (IOException e)
        {
            // Ignore
        }

        return true;
    }

    /**
     * Create a new label.
     * @param name name of the label to create.
     */
    private void createLabel(String name)
    {
        if (this.library.addLabel(name).getCode() == 0)
        {
            this.searchError.setText("Label created successfully.");
        }
        else
        {
            this.searchError.setText("Failed to create label.");
        }
    }

    /** Handle finding of the cover image. */
    private void findCover()
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Cover Image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg")
        );

        File cover = chooser.showOpenDialog(null);
        if (cover != null)
        {
            try
            {
                this.coverScrollPane.setContent(
                        new ImageView(new Image(new FileInputStream(cover)))
                );

                this.handleImage(cover);
            }
            catch (FileNotFoundException e)
            {
                this.coverPath = null;
            }
        }
    }

    /**
     * Copy the cover image to a file in user's home directory,
     * and set the cover path to the new path.
     */
    private void handleImage(File coverImage)
    {
        new File(System.getProperty("user.home") + "/.config/agatha/" + this.library.getUsername()).mkdirs();

        try
        {
            File copyTo;
            do {
                copyTo = new File(System.getProperty("user.home") + "/.config/agatha/" + this.library.getUsername() + "/" + this.randomName());
            } while (copyTo.exists());

            Files.copy(new FileInputStream(coverImage), copyTo.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            this.coverPath = copyTo.getAbsolutePath();
        }
        catch (IOException e)
        {
            this.coverPath = null;
        }
    }

    /** Generate a random 8 char string to use as a name for cover images. */
    private String randomName()
    {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++)
        {
            builder.append(random.nextInt(10));
        }

        return builder.toString();
    }
}
