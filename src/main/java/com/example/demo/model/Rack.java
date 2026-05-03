package com.example.demo.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "racks")
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String section;

    @Column(nullable = false)
    private Integer columnNumber;

    @OneToMany(mappedBy = "rack")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Book> books;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public Integer getColumnNumber() { return columnNumber; }
    public void setColumnNumber(Integer columnNumber) { this.columnNumber = columnNumber; }
    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }
}
