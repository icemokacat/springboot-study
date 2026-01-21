package moka.board.like.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import moka.board.common.snowflake.Snowflake;
import moka.board.like.repository.ArticleLikeCountRepository;
import moka.board.like.repository.ArticleLikeRepository;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
	private final Snowflake snowflake = new Snowflake();
	private final ArticleLikeRepository articleLikeRepository;
	private final ArticleLikeCountRepository articleLikeCountRepository;
}
