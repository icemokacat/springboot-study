package moka.board.comment.service.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.ToString;
import moka.board.comment.entity.Comment;

@Getter
@ToString
public class CommentResponse {
	private Long commentId;
	private String content;
	private Long parentCommentId;
	private Long articleId;
	private Long writerId;
	private Boolean deleted;
	private LocalDateTime createAt;

	public static CommentResponse from(Comment entity){
		CommentResponse response = new CommentResponse();
		response.commentId = entity.getCommentId();
		response.content = entity.getContent();
		response.parentCommentId = entity.getParentCommentId();
		response.articleId = entity.getArticleId();
		response.writerId = entity.getWriterId();
		response.deleted = entity.getDeleted();
		response.createAt = entity.getCreatedAt();
		return response;
	}
}
