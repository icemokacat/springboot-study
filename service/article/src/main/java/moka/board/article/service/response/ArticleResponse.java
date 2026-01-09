package moka.board.article.service.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.ToString;
import moka.board.article.entity.Article;

@Getter
@ToString
public class ArticleResponse {
	private Long articleId;
	private String title;
	private String content;
	private Long boardId; // shard key
	private Long writerId;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;

	public static ArticleResponse from(Article entity){
		ArticleResponse response = new ArticleResponse();
		response.articleId = entity.getArticleId();
		response.title = entity.getTitle();
		response.content = entity.getContent();
		response.boardId = entity.getBoardId();
		response.writerId = entity.getWriterId();
		response.createdAt = entity.getCreatedAt();
		response.modifiedAt =  entity.getModifiedAt();
		return response;
	}
}
