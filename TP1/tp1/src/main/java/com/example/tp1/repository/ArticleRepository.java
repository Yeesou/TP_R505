package com.example.tp1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tp1.model.Article;
import com.example.tp1.model.User;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByAuthor(User author);
    List<Article> findByAuthorOrderByPublicationDateDesc(User author);
    List<Article> findAllByOrderByPublicationDateDesc();
}
