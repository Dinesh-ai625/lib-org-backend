package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private Integer totalCopies;

    @Column(nullable = false)
    private Integer availableCopies;

    @Column(length = 1000)
    private String coverUrl;

    @Column(length = 2000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "rack_id")
    private Rack rack;

    @Column(name = "position")
    private Integer position;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }
    public Integer getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(Integer availableCopies) { this.availableCopies = availableCopies; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Rack getRack() { return rack; }
    public void setRack(Rack rack) { this.rack = rack; }
    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }
}
