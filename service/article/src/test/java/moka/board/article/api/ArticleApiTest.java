package moka.board.article.api;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import moka.board.article.service.response.ArticlePageResponse;
import moka.board.article.service.response.ArticleResponse;

public class ArticleApiTest {
	RestClient restClient = RestClient.create("http://localhost:9000");

	@Test
	void createTest() {
		ArticleCreateRequest request = new ArticleCreateRequest(
			"Test Title",
			"Test Content",
			1L,
			1L
		);

		ArticleResponse response = create(request);

		System.out.println("Response: " + response);
	}

	@Test
	void readTest(){
		Long id = 267818972899192832L;
		ArticleResponse response = read(id);

		System.out.println("Response: " + response);
	}

	@Test
	void updateTest(){
		Long id = 267818972899192832L;
		ArticleUpdateRequest request = new ArticleUpdateRequest(
			"Updated Title",
			"Updated Content"
		);
		ArticleResponse response = update(id, request);

		System.out.println("Response: " + response);
	}

	@Test
	void deleteTest(){
		Long id = 267818972899192832L;
		delete(id);
	}

	@Test
	void readAllTest(){
		ArticlePageResponse result = restClient.get()
			.uri("/v1/articles?boardId={boardId}&page={page}&pageSize={pageSize}", 1L, 50000L, 30L)
			.retrieve()
			.body(ArticlePageResponse.class);

		Assertions.assertNotNull(result);
		System.out.println("Read all articles test completed. count = " + result.getArticleCount());
		for(var article : result.getArticles()){
			System.out.println("Article: " + article);
		}
	}

	@Test
	void readAllInfiniteScrollTest(){
		List<ArticleResponse> articles1 = restClient.get()
			.uri("/v1/articles/infinite-scroll?boardId={boardId}&pageSize={pageSize}", 1L, 30L)
			.retrieve()
			.body(new ParameterizedTypeReference<>() {

			});

		Assertions.assertNotNull(articles1);
		System.out.println("Read all infinite scroll articles test completed. count = " + articles1.size());
		for(ArticleResponse article : articles1) {
			System.out.println("Article: " + article);
		}

		Long lastArticleId = articles1.getLast().getArticleId();

		List<ArticleResponse> articles2 = restClient.get()
			.uri("/v1/articles/infinite-scroll?boardId={boardId}&pageSize={pageSize}&lastArticleId={lastArticleId}",
				1L, 30L, lastArticleId)
			.retrieve()
			.body(new ParameterizedTypeReference<>() {
			});

		Assertions.assertNotNull(articles2);
		System.out.println("Read all infinite scroll articles (with lastArticleId) test completed. count = " + articles2.size());
		for(ArticleResponse article : articles2) {
			System.out.println("Article: " + article);
		}
	}

	ArticleResponse create(ArticleCreateRequest request){
		return restClient.post()
			.uri("/v1/articles")
			.body(request)
			.retrieve()
			.body(ArticleResponse.class);
	}
	ArticleResponse read(Long id){
		return restClient.get()
			.uri("/v1/articles/{id}", id)
			.retrieve()
			.body(ArticleResponse.class);
	}
	ArticleResponse update(Long id, ArticleUpdateRequest request){
		return restClient.put()
			.uri("/v1/articles/{id}", id)
			.body(request)
			.retrieve()
			.body(ArticleResponse.class);
	}
	ArticleResponse delete(Long id){
		return restClient.delete()
			.uri("/v1/articles/{id}", id)
			.retrieve()
			.body(ArticleResponse.class);
	}


	@Getter
	@AllArgsConstructor
	static class ArticleCreateRequest {
		private String title;
		private String content;
		private Long boardId;
		private Long writerId;
	}

	@Getter
	@AllArgsConstructor
	static class ArticleUpdateRequest {
		private String title;
		private String content;
	}


}
