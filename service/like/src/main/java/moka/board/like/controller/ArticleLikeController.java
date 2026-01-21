package moka.board.like.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import moka.board.like.service.ArticleLikeService;

@RestController
@RequiredArgsConstructor
public class ArticleLikeController {

	private final ArticleLikeService articleLikeService;

}
