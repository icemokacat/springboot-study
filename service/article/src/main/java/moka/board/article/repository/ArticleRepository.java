package moka.board.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import moka.board.article.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
