package oel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

public class LibraryCatalogSystem {

    static class Book {
        String title;
        String author;
        String genre;

        public Book(String title, String author, String genre) {
            this.title = title.trim();
            this.author = author.trim();
            this.genre = genre.trim();
        }

        @Override
        public String toString() {
            return String.format("Title: \"%s\", Author: %s, Genre: %s", title, author, genre);
        }
    }

    public static void main(String[] args) {
        List<Book> books = new ArrayList<>();
        books.add(new Book("The Hobbit", "J.R.R. Tolkien", "Fantasy"));
        books.add(new Book("1984", "George Orwell", "Science Fiction"));
        books.add(new Book("The Da Vinci Code", "Dan Brown", "Mystery"));
        books.add(new Book("To Kill a Mockingbird", "Harper Lee", "Fiction"));
        books.add(new Book("A Brief History of Time", "Stephen Hawking", "Non-fiction"));
        books.add(new Book("The Great Gatsby", "F. Scott Fitzgerald", "Fiction"));

        List<String> searchHistory = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n==== Library Catalog Menu ====");
            System.out.println("1. View all books (sorted by title)");
            System.out.println("2. Search books");
            System.out.println("3. Add new book");
            System.out.println("4. View recommendations");
            System.out.println("5. Exit");
            System.out.print("Choose an option (1-5): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    List<Book> sortedBooks = sortBooks(books, "title_asc");
                    printBooks(sortedBooks);
                    break;
                case "2":
                    System.out.print("Enter search term: ");
                    String term = scanner.nextLine();
                    searchHistory.add(term.toLowerCase());
                    List<Book> results = searchBooks(books, term);
                    printBooks(results);
                    break;
                case "3":
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter author name: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter genre: ");
                    String genre = scanner.nextLine();
                    books.add(new Book(title, author, genre));
                    System.out.println("Book added successfully.");
                    break;
                case "4":
                    printRecommendations(books, searchHistory);
                    break;
                case "5":
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }

    static void printBooks(List<Book> books) {
        if (books == null || books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        for (Book b : books) {
            System.out.println(" - " + b.toString());
        }
    }

    static List<Book> searchBooks(List<Book> books, String term) {
        if (term == null || term.isEmpty()) return new ArrayList<>();
        String lowerTerm = term.toLowerCase();
        return books.stream()
            .filter(b -> b.title.toLowerCase().contains(lowerTerm)
                      || b.author.toLowerCase().contains(lowerTerm)
                      || b.genre.toLowerCase().contains(lowerTerm))
            .collect(Collectors.toList());
    }

    static List<Book> sortBooks(List<Book> books, String criterion) {
        List<Book> sorted = new ArrayList<>(books);
        String[] parts = criterion.split("_");
        if (parts.length != 2) return sorted;
        String key = parts[0];
        String order = parts[1];

        Comparator<Book> comparator;

        switch (key) {
            case "title":
                comparator = Comparator.comparing(b -> b.title.toLowerCase());
                break;
            case "author":
                comparator = Comparator.comparing(b -> b.author.toLowerCase());
                break;
            case "genre":
                comparator = Comparator.comparing(b -> b.genre.toLowerCase());
                break;
            default:
                return sorted;
        }
        if ("desc".equals(order)) {
            comparator = comparator.reversed();
        }

        sorted.sort(comparator);
        return sorted;
    }

    static void printRecommendations(List<Book> books, List<String> searchHistory) {
        if (searchHistory == null || searchHistory.isEmpty()) {
            System.out.println("No search history available.");
            return;
        }

        Map<String, Integer> genreCount = new HashMap<>();
        Map<String, Integer> authorCount = new HashMap<>();

        for (String term : searchHistory) {
            String t = term.toLowerCase();
            for (Book b : books) {
                if (b.genre.toLowerCase().equals(t)) {
                    genreCount.put(b.genre, genreCount.getOrDefault(b.genre, 0) + 1);
                }
                if (b.author.toLowerCase().contains(t)) {
                    authorCount.put(b.author, authorCount.getOrDefault(b.author, 0) + 1);
                }
            }
        }

        String recommendedGenre = genreCount.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);

        String recommendedAuthor = authorCount.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);

        if (recommendedGenre == null && recommendedAuthor == null) {
            System.out.println("No personalized recommendations found.");
            return;
        }

        if (recommendedGenre != null) {
            System.out.println("- You might enjoy more books in the genre: " + recommendedGenre);
        }
        if (recommendedAuthor != null) {
            System.out.println("- You may like more books by author: " + recommendedAuthor);
        }
    }
}
