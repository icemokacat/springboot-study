package moka.board.comment.service;

import static java.util.function.Predicate.*;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import moka.board.comment.entity.Comment;
import moka.board.comment.repository.CommentRepository;
import moka.board.comment.service.request.CommentCreateRequest;
import moka.board.comment.service.response.CommentPageResponse;
import moka.board.comment.service.response.CommentResponse;
import moka.board.common.snowflake.Snowflake;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final Snowflake snowflake = new Snowflake();
	private final CommentRepository commentRepository;

	@Transactional
	public CommentResponse create(CommentCreateRequest request){
		Comment parentComment = findParent(request);
		Comment savedComment = commentRepository.save(
				Comment.create(
						snowflake.nextId(),
						request.getContent(),
						parentComment == null ? null : parentComment.getCommentId(),
						request.getArticleId(),
						request.getWriterId()
				)
		);
		return CommentResponse.from(savedComment);
	}

	private Comment findParent(CommentCreateRequest request) {
		Long parentCommentId = request.getParentCommentId();
		if (parentCommentId == null) {
			return null;
		}
		return commentRepository.findById(parentCommentId)
			.filter(not(Comment::getDeleted))
			.filter(Comment::isRoot)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 댓글입니다. parentCommentId: " + parentCommentId));
	}

	public CommentResponse read(Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다. commentId: " + commentId));
		return CommentResponse.from(comment);
	}

	@Transactional
	public void delete(Long commentId){
		commentRepository.findById(commentId)
			// 댓글이 논리 삭제되지 않은 상태인지 확인
			.filter(not(Comment::getDeleted))
			.ifPresent(comment -> {
				if(hasChildren(comment)) {
					// 하위 댓글이 있다면 논리 삭제
					comment.delete();
				} else {
					// 하위 댓글이 없다면 물리 삭제
					delete(comment);
				}
			});
	}

	private boolean hasChildren(@NonNull Comment comment) {
		return commentRepository.countBy(comment.getArticleId(), comment.getCommentId(), 2L) == 2;
	}

	private void delete(@NonNull Comment comment){
		commentRepository.delete(comment);
		if( !comment.isRoot() ) {
			// 부모 댓글이 존재하는 경우 부모 댓글 조회
			commentRepository.findById(comment.getParentCommentId())
				// 부모 댓글이 논리 삭제된 상태인지 확인
				.filter(Comment::getDeleted)
				// 부모 댓글이 하위 댓글이 없는지 확인
				.filter(not(this::hasChildren))
				// 재귀 삭제
				.ifPresent(this::delete);
		}
	}

	public CommentPageResponse readAll(Long articleId, Long page, Long pageSize) {
		long offset = (page - 1) * pageSize;
		long limit 	= PageLimitCalculator.calculatePageLimit(page, pageSize, 10L);
		List<CommentResponse> comments = commentRepository.findAll(articleId, offset, pageSize)
			.stream()
			.map(CommentResponse::from)
			.toList();
		Long commentCount = commentRepository.count(articleId, limit);
		return CommentPageResponse.of(comments, commentCount);
	}

	public List<CommentResponse> readAll(Long articleId, Long lastCommentId, Long lastParentCommentId, Long limit){
		List<Comment> dbResult = lastParentCommentId == null || lastCommentId == null
			? commentRepository.findAllInfiniteScroll(articleId, limit)
			: commentRepository.findAllInfiniteScroll(articleId, lastParentCommentId, lastCommentId, limit);

		return dbResult.stream()
			.map(CommentResponse::from)
			.toList();
	}

}
