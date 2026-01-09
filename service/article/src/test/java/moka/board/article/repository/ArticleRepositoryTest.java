package moka.board.article.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;
import moka.board.article.entity.Article;

@Slf4j
@SpringBootTest
class ArticleRepositoryTest {

	@Autowired
	ArticleRepository articleRepository;

	@Test
	void findAllTest(){
		var articles = articleRepository.findAll(1L, 1499970L, 30L);
		assertNotNull(articles);
		log.info("Articles size: {}", articles.size());
		for(var article : articles){
			log.info("Article: {}", article);
		}
	}

	@Test
	void countAllTest(){
		Long count = articleRepository.count(1L, 1499970L);
		assertNotNull(count);
		log.info("Article count: {}", count);
	}

	@Test
	void findInfiniteScrollTest(){
		List<Article> articles = articleRepository.findAllInfiniteScroll(1L, 30L);
		assertNotNull(articles);
		log.info("Articles size: {}", articles.size());
		for(var article : articles) {
			log.info("Article: {}", article);
		}
		// 마지막 articleId 기준으로 더 불러오기
		if(!articles.isEmpty()) {
			Long lastArticleId = articles.getLast().getArticleId();
			List<Article> moreArticles = articleRepository.findAllInfiniteScroll(1L, 30L,lastArticleId);
			assertNotNull(moreArticles);
			log.info("More Articles size: {}", moreArticles.size());
			for (var article : moreArticles) {
				log.info("More Article: {}", article);
			}
		}
	}

}