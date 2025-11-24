package com.example.tp1.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tp1.model.Article;
import com.example.tp1.repository.ArticleRepository;
import com.example.tp1.repository.UserRepository;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    // Créer un article
    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody ArticleRequest request) {
        return userRepository.findById(request.getAuthorId())
                .map(author -> {
                    Article article = new Article();
                    article.setContent(request.getContent());
                    article.setAuthor(author);
                    article.setPublicationDate(LocalDateTime.now());
                    Article savedArticle = articleRepository.save(article);
                    return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    // Récupérer tous les articles
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        return ResponseEntity.ok(articleRepository.findAllByOrderByPublicationDateDesc());
    }

    // Récupérer un article par ID
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        return articleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Récupérer les articles d'un auteur
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Article>> getArticlesByAuthor(@PathVariable Long authorId) {
        return userRepository.findById(authorId)
                .map(author -> ResponseEntity.ok(articleRepository.findByAuthorOrderByPublicationDateDesc(author)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Mettre à jour un article
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody ArticleRequest request) {
        return articleRepository.findById(id)
                .map(article -> {
                    article.setContent(request.getContent());
                    return ResponseEntity.ok(articleRepository.save(article));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Supprimer un article
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        if (articleRepository.existsById(id)) {
            articleRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Liker un article
    @PostMapping("/{id}/like")
    public ResponseEntity<Article> likeArticle(@PathVariable Long id, @RequestParam Long userId) {
        return articleRepository.findById(id)
                .flatMap(article -> userRepository.findById(userId)
                        .map(user -> {
                            // Retirer le dislike si présent
                            article.getDislikedByUsers().remove(user);
                            user.getDislikedArticles().remove(article);
                            
                            // Ajouter le like
                            article.getLikedByUsers().add(user);
                            user.getLikedArticles().add(article);
                            
                            userRepository.save(user);
                            return ResponseEntity.ok(articleRepository.save(article));
                        }))
                .orElse(ResponseEntity.notFound().build());
    }

    // Disliker un article
    @PostMapping("/{id}/dislike")
    public ResponseEntity<Article> dislikeArticle(@PathVariable Long id, @RequestParam Long userId) {
        return articleRepository.findById(id)
                .flatMap(article -> userRepository.findById(userId)
                        .map(user -> {
                            // Retirer le like si présent
                            article.getLikedByUsers().remove(user);
                            user.getLikedArticles().remove(article);
                            
                            // Ajouter le dislike
                            article.getDislikedByUsers().add(user);
                            user.getDislikedArticles().add(article);
                            
                            userRepository.save(user);
                            return ResponseEntity.ok(articleRepository.save(article));
                        }))
                .orElse(ResponseEntity.notFound().build());
    }

    // Retirer un like/dislike
    @DeleteMapping("/{id}/reaction")
    public ResponseEntity<Article> removeReaction(@PathVariable Long id, @RequestParam Long userId) {
        return articleRepository.findById(id)
                .flatMap(article -> userRepository.findById(userId)
                        .map(user -> {
                            article.getLikedByUsers().remove(user);
                            user.getLikedArticles().remove(article);
                            article.getDislikedByUsers().remove(user);
                            user.getDislikedArticles().remove(article);
                            
                            userRepository.save(user);
                            return ResponseEntity.ok(articleRepository.save(article));
                        }))
                .orElse(ResponseEntity.notFound().build());
    }

    // Classe interne pour les requêtes
    public static class ArticleRequest {
        private String content;
        private Long authorId;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Long getAuthorId() {
            return authorId;
        }

        public void setAuthorId(Long authorId) {
            this.authorId = authorId;
        }
    }
}
