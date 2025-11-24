package com.example.tp1.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 5000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime publicationDate;

    @JsonIgnoreProperties({"password", "articles", "likedArticles", "dislikedArticles"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @JsonIgnoreProperties({"password", "articles", "likedArticles", "dislikedArticles"})
    @ManyToMany(mappedBy = "likedArticles")
    private Set<User> likedByUsers = new HashSet<>();

    @JsonIgnoreProperties({"password", "articles", "likedArticles", "dislikedArticles"})
    @ManyToMany(mappedBy = "dislikedArticles")
    private Set<User> dislikedByUsers = new HashSet<>();

    // Constructeurs
    public Article() {
    }

    public Article(String content, User author) {
        this.content = content;
        this.author = author;
        this.publicationDate = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Set<User> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(Set<User> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    public Set<User> getDislikedByUsers() {
        return dislikedByUsers;
    }

    public void setDislikedByUsers(Set<User> dislikedByUsers) {
        this.dislikedByUsers = dislikedByUsers;
    }

    // Méthodes utilitaires pour compter likes/dislikes
    public int getLikesCount() {
        return likedByUsers.size();
    }

    public int getDislikesCount() {
        return dislikedByUsers.size();
    }
}
