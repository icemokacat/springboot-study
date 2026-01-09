package moka.board.article.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import moka.board.article.entity.Article;
import moka.board.article.repository.ArticleRepository;
import moka.board.article.service.request.ArticleCreateRequest;
import moka.board.article.service.request.ArticleUpdateRequest;
import moka.board.article.service.response.ArticleResponse;
import moka.board.common.snowflake.Snowflake;

@Service
@RequiredArgsConstructor
public class ArticleService {
	private final Snowflake snowflake = new Snowflake();
	private final ArticleRepository articleRepository;

	@Transactional
	public ArticleResponse create(ArticleCreateRequest request){
		Article articleRequest = Article.create(snowflake.nextId(),
										request.getTitle(),
										request.getContent(),
										request.getBoardId(),
										request.getWriterId());
		Article savedArticle = articleRepository.save(articleRequest);
		return ArticleResponse.from(savedArticle);
	}

	@Transactional
	public ArticleResponse update(Long articleId, ArticleUpdateRequest request){
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. articleId: " + articleId));
		article.update(request.getTitle(), request.getContent());
		Article updatedArticle = articleRepository.save(article);
		return ArticleResponse.from(updatedArticle);
	}

	public ArticleResponse read(Long articleId) {
		Article article = articleRepository.findById(articleId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다. articleId: " + articleId));
		return ArticleResponse.from(article);
	}

	@Transactional
	public void delete(Long articleId) {
		articleRepository.deleteById(articleId);
	}

}
