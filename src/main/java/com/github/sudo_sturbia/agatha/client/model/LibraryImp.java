package com.github.sudo_sturbia.agatha.client.model;

import com.github.sudo_sturbia.agatha.client.model.book.Book;

import java.util.Collection;
import java.util.List;

/**
 * LibraryImp is an implementation of Library interface.
 */
public class LibraryImp implements Library
{
    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public List<String> getNamesOfBooks() {
        return null;
    }

    @Override
    public List<String> getNamesOfBooksWithLabel(String label) {
        return null;
    }

    @Override
    public Book getBookWithName(String name) {
        return null;
    }

    @Override
    public void addBook(Book book) {

    }

    @Override
    public void addBook(Book book, String label) {

    }

    @Override
    public void addBooks(Collection<Book> books) {

    }

    @Override
    public void addBooks(Collection<Book> books, String label) {

    }

    @Override
    public void addLabel(String label) {

    }

    @Override
    public void addLabelToBook(String bookName, String label) throws IllegalArgumentException {

    }

    @Override
    public void deleteBookWithName(String name) {

    }

    @Override
    public void deleteBooksWithLabel(String label) {

    }

    @Override
    public void deleteLabel(String label) {

    }

    @Override
    public void deleteLabelFromBook(String bookName, String label) {

    }
}
