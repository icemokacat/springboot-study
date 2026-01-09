package moka.board.article.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

import lombok.AllArgsConstructor;
import lombok.Getter;
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
