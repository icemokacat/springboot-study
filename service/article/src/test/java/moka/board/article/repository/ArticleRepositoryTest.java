package moka.board.article.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

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

}