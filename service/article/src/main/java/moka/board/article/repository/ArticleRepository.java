package moka.board.article.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import moka.board.article.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

	@Query(
		value = "select article.article_id, article.title, article.content, article.board_id, article.writer_id, "
			+ "article.created_at, article.modified_at " +
			"from ("
			+ "    select article_id from article "+
			"    where board_id = :boardId "+
			"    order by article_id desc "+
			"    limit :limit offset :offset"
			+ ") as sub "+
			"left join article on sub.article_id = article.article_id ",
		nativeQuery = true
	)
	List<Article> findAll(
		@Param("boardId") Long boardId,
		@Param("offset") Long offset,
		@Param("limit") Long limit
	);

	@Query(
		value = "select count(*) from (" +
				"    select article_id from article where board_id = :boardId limit :limit"+
				") as sub ",
		nativeQuery = true
	)
	Long count(@Param("boardId") Long boardId, @Param("limit") Long limit);

	@Query(
		value = "select article.article_id, article.title, article.content, article.board_id, article.writer_id, "
			+ "article.created_at, article.modified_at " +
			"from ("
			+ "    select article_id from article "+
			"    where board_id = :boardId "+
			"    order by article_id desc "+
			"    limit :limit "
			+ ") as sub "+
			"left join article on sub.article_id = article.article_id ",
		nativeQuery = true
	)
	List<Article> findAllInfiniteScroll(@Param("boardId") Long boardId, @Param("limit") Long limit);

	@Query(
		value = "select article.article_id, article.title, article.content, article.board_id, article.writer_id, "
			+ "article.created_at, article.modified_at " +
			"from ("
			+ "    select article_id from article "+
			"    where board_id = :boardId and article_id < :lastArticleId "+
			"    order by article_id desc "+
			"    limit :limit "
			+ ") as sub "+
			"left join article on sub.article_id = article.article_id ",
		nativeQuery = true
	)
	List<Article> findAllInfiniteScroll(@Param("boardId") Long boardId, @Param("limit") Long limit, @Param("lastArticleId") Long lastArticleId);

}
