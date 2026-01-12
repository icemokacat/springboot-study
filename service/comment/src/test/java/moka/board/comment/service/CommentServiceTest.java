package moka.board.comment.service;

import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import moka.board.comment.entity.Comment;
import moka.board.comment.repository.CommentRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
	@InjectMocks
	CommentService commentService;
	@Mock
	CommentRepository commentRepository;

	@Test
	@DisplayName("삭제할 댓글이 자식 있으면, 삭제 표시만 한다.")
	void deleteShouldMarkDeletedIfHashChildren() {
		/**
		 * ex)
		 * c1 -- c2
		 *    \-- c3
		 *    c1 삭제 시, c2, c3 가 존재하므로 삭제 표시만 한다.
		 * */
		// given
		Long articleId = 1L;
		Long commentId = 2L;
		Comment comment = createComment(articleId, commentId);
		// commentId 2L 를 호출했을때 comment 반환 (create 된)
		given(commentRepository.findById(commentId))
			.willReturn(Optional.of(comment));

		// 자식 댓글이 존재하는 상황을 만듦
		given(commentRepository.countBy(articleId, commentId, 2L)).willReturn(2L);

		// when
		commentService.delete(commentId);

		// then
		verify(comment).delete();
	}

	@Test
	@DisplayName("하위 댓글이 삭제되고, 삭제되지 않은 부모면, 하위 댓글만 삭제한다.")
	void deleteShouldHardDeletedChildOnlyIfNotDeletedParent() {
		// given
		Long articleId = 1L;
		Long commentId = 2L;
		Long parentCommentId = 1L;

		// 상위 댓글이 있는 댓글
		Comment comment = createComment(articleId, commentId, parentCommentId);
		given(comment.isRoot()).willReturn(false);

		// 그 상위 댓글(부모)는 삭제되지 않은 상태
		Comment parent = mock(Comment.class);
		given(parent.getDeleted()).willReturn(false);

		// 조회될 수 있는 상황 설정
		given(commentRepository.findById(commentId))
			.willReturn(Optional.of(comment));
		
		// 자식 댓글이 존재하지 않는 상황을 만듦
		given(commentRepository.countBy(articleId, commentId, 2L)).willReturn(1L);
		
		// 부모호출시에는 부모가 호출됨
		// 상황 : delete에서 commentRepository.findById(comment.getParentCommentId())
		// 부모댓글을 확인하는 경우임
		given(commentRepository.findById(parentCommentId))
			.willReturn(Optional.of(parent));

		// 요약 : 자기자신은 아직 삭제되지 않았고, 부모도 삭제되지 않은 상태
		// 또한 자식 댓글은 없는 상태

		// when
		commentService.delete(commentId);

		// then
		// 그러면 자기자신은 하위자식이 없기 때문에 hard delete 되고
		verify(commentRepository).delete(comment);
		// 상위 댓글 삭제는 호출되지 않음
		verify(commentRepository, never()).delete(parent);

		/**
		 * 요약
		 * c1 -- c2
		 *    \-- c3
		 *    c2 삭제 시, c2 는 하위 댓글이 없으므로 물리 삭제
		 *    c1 은 삭제되지 않은 상태이므로 삭제되지 않는다.
		 * */
	}

	@Test
	@DisplayName("하위 댓글이 삭제되고, 삭제된 부모면, 재귀적으로 모두 삭제한다.")
	void deleteShouldDeleteAllRecursivelyIfDeletedParent() {
		// given
		Long articleId = 1L;
		Long commentId = 2L;
		Long parentCommentId = 1L;

		// 상위 댓글이 있는 댓글
		Comment comment = createComment(articleId, commentId, parentCommentId);
		given(comment.isRoot()).willReturn(false);

		// 그 상위 댓글(부모)는 삭제된 상태
		Comment parent = createComment(articleId, parentCommentId);
		given(parent.isRoot()).willReturn(true);
		given(parent.getDeleted()).willReturn(true);

		// 조회될 수 있는 상황 설정
		given(commentRepository.findById(commentId))
			.willReturn(Optional.of(comment));

		// 자식 댓글이 존재하지 않는 상황을 만듦
		given(commentRepository.countBy(articleId, commentId, 2L)).willReturn(1L);

		// 부모호출시에는 부모가 호출됨
		// 상황 : delete에서 commentRepository.findById(comment.getParentCommentId())
		// 부모댓글을 확인하는 경우임
		given(commentRepository.findById(parentCommentId))
			.willReturn(Optional.of(parent));
		// 그 부모 댓글도 (나 아닌 다른) 자식 댓글이 없는 상황
		given(commentRepository.countBy(articleId, parentCommentId, 2L)).willReturn(1L);

		// 요약 : 자기자신은 아직 삭제되지 않았고, 부모는 삭제된 상태
		// 또한 자식 댓글은 없는 상태

		// when
		commentService.delete(commentId);

		// then
		// 그러면 자기자신은 하위자식이 없기 때문에 hard delete 되고
		verify(commentRepository).delete(comment);
		// 상위 댓글도 하위자식이 없으므로 hard delete 됨
		verify(commentRepository).delete(parent);

		/**
		 * 요약
		 * c1 -- c2
		 *    \-- c3
		 *    c2 삭제 시, c2 는 하위 댓글이 없으므로 물리 삭제
		 *    c1 은 삭제되지 않은 상태이므로 삭제되지 않는다.
		 * */
	}

	private Comment createComment(Long articleId, Long commentId){
		Comment comment = mock(Comment.class);
		given(comment.getArticleId()).willReturn(articleId);
		given(comment.getCommentId()).willReturn(commentId);
		return comment;
	}

	private Comment createComment(Long articleId, Long commentId, Long parentCommentId){
		Comment comment = createComment(articleId, commentId);
		given(comment.getParentCommentId()).willReturn(parentCommentId);
		return comment;
	}

}