package moka.board.article.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PageLimitCalculatorTest {

	@Test
	void calculatePageLimitTest() {
		// case1
		Long page = 1L;
		Long pageSize = 30L;
		Long movablePageCount = 10L;

		Long expectedLimit = 301L; // (((7 - 1) / 5) + 1) * 5 * 10 + 1 = 60

		calculatePageLimitTest(page, pageSize, movablePageCount, expectedLimit);

		// case2
		page = 7L;
		pageSize = 30L;
		movablePageCount = 10L;
		expectedLimit = 301L; // (((7 - 1) / 5) + 1) * 5 * 10 + 1 = 60

		calculatePageLimitTest(page, pageSize, movablePageCount, expectedLimit);
	}

	void calculatePageLimitTest(Long page, Long pageSize, Long movablePageCount, Long expectedLimit) {
		Long actualLimit = PageLimitCalculator.calculatePageLimit(page, pageSize, movablePageCount);
		assertThat(actualLimit).isEqualTo(expectedLimit);
	}

}