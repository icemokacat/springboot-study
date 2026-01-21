package moka.board.comment.api;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import moka.board.comment.service.response.CommentPageResponse;
import moka.board.comment.service.response.CommentResponse;

public class CommentApiTest {
	RestClient restClient = RestClient.create("http://localhost:9001");

	@Test
	void create() {
		CommentCreateRequest request1 = new CommentCreateRequest(1L,"TestContent1",null,1L);
		CommentResponse response1 = createResponse(request1);


		CommentCreateRequest request2 = new CommentCreateRequest(1L,"TestContent2",response1.getCommentId(),1L);
		CommentResponse response2 = createResponse(request2);

		CommentCreateRequest request3 = new CommentCreateRequest(1L,"TestContent3",response1.getCommentId(),1L);
		CommentResponse response3 = createResponse(request3);

		System.out.println("CommentId: " + response1.getCommentId());
		System.out.println("\tCommentId: " + response2.getCommentId());
		System.out.println("\tCommentId: " + response3.getCommentId());

		// commentId=270099001690845184
 		// commentId=270099002676506624
 		// commentId=270099002877833216
	}

	CommentResponse createResponse(CommentCreateRequest request){
		return restClient.post()
			.uri("/v1/comments")
			.body(request)
			.retrieve()
			.body(CommentResponse.class);
	}

	@Test
	void read(){
		CommentResponse response = restClient.get()
			.uri("/v1/comments/{commentId}", 270099001690845184L)
			.retrieve()
			.body(CommentResponse.class);

		System.out.println("response retrieved : " + response);
	}

	@Test
	void delete() {
		// commentId=270099001690845184
		//   commentId=270099002676506624
		//   commentId=270099002877833216
		restClient.delete()
			.uri("/v1/comments/{commentId}", 270099002877833216L)
			.retrieve();
	}

	@Test
	void readAll() {
		CommentPageResponse response = restClient.get()
			.uri("/v1/comments?articleId=1&page=1&pageSize=10")
			.retrieve()
			.body(CommentPageResponse.class);

		System.out.println("response.getCommentCount() = " + response.getCommentCount());
		for (CommentResponse comment : response.getComments()) {
			if (!comment.getCommentId().equals(comment.getParentCommentId())) {
				System.out.print("\t");
			}
			System.out.println("comment.getCommentId() = " + comment.getCommentId());
		}

		/*response.getCommentCount() = 101
		comment.getCommentId() = 270102510963855360
			comment.getCommentId() = 270102511215513606
		comment.getCommentId() = 270102510963855361
			comment.getCommentId() = 270102511215513605
		comment.getCommentId() = 270102510963855362
			comment.getCommentId() = 270102511215513609
		comment.getCommentId() = 270102510963855363
			comment.getCommentId() = 270102511215513604
		comment.getCommentId() = 270102510963855364
			comment.getCommentId() = 270102511215513602*/
	}

	@Test
	void readAllInfiniteScroll() {
		List<CommentResponse> responses1 = restClient.get()
			.uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
			.retrieve()
			.body(new ParameterizedTypeReference<List<CommentResponse>>() {
			});

		System.out.println("firstPage");
		for (CommentResponse comment : responses1) {
			if (!comment.getCommentId().equals(comment.getParentCommentId())) {
				System.out.print("\t");
			}
			System.out.println("comment.getCommentId() = " + comment.getCommentId());
		}

		Long lastParentCommentId = responses1.getLast().getParentCommentId();
		Long lastCommentId = responses1.getLast().getCommentId();

		/*
		comment.getCommentId() = 270102510963855360
			comment.getCommentId() = 270102511215513606
		comment.getCommentId() = 270102510963855361
			comment.getCommentId() = 270102511215513605
		comment.getCommentId() = 270102510963855362
		*/

		List<CommentResponse> responses2 = restClient.get()
			.uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s"
				.formatted(lastParentCommentId, lastCommentId))
			.retrieve()
			.body(new ParameterizedTypeReference<List<CommentResponse>>() {
			});

		System.out.println("secondPage");
		for (CommentResponse comment : responses2) {
			if (!comment.getCommentId().equals(comment.getParentCommentId())) {
				System.out.print("\t");
			}
			System.out.println("comment.getCommentId() = " + comment.getCommentId());
		}
		/*
			comment.getCommentId() = 270102511215513609
		comment.getCommentId() = 270102510963855363
			comment.getCommentId() = 270102511215513604
		comment.getCommentId() = 270102510963855364
			comment.getCommentId() = 270102511215513602
		*/
	}

	@Getter
	@AllArgsConstructor
	public static class CommentCreateRequest {
		private Long articleId;
		private String content;
		private Long parentCommentId;
		private Long writerId;
	}

}
