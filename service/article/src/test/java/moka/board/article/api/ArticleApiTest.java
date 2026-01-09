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

	ArticleResponse create(ArticleCreateRequest request){
		return restClient.post()
			.uri("/v1/articles")
			.body(request)
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
