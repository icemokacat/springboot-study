package moka.board.comment.data;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import moka.board.comment.entity.Comment;
import moka.board.common.snowflake.Snowflake;

@SpringBootTest
public class DataInitializer {
	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	TransactionTemplate transactionTemplate;
	Snowflake snowflake = new Snowflake();
	CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);

	static final int BULK_INSERT_SIZE = 2000;
	static final int EXECUTE_COUNT = 6000;

	@Test
	void initialize() throws InterruptedException {

		ExecutorService executorService = Executors.newFixedThreadPool(10);

		try{
			for (int i = 0; i < EXECUTE_COUNT; i++) {
				executorService.submit(() -> {
					insert();
					latch.countDown();
					System.out.println("Remaining Latch: " + latch.getCount());
				});
			}
			latch.await();
		}finally {
			executorService.shutdown();
			boolean result = executorService.awaitTermination(1, TimeUnit.MINUTES);
			System.out.println("Executor Service Terminated: " + result);
		}
	}

	void insert(){
		transactionTemplate.executeWithoutResult(status -> {
			Comment prev = null;
			for(int i = 0; i < BULK_INSERT_SIZE; i++){
				Comment forSave = Comment.create(
					snowflake.nextId(),
					"content"+i,
					i % 2 == 0 ? null : prev.getCommentId(),
					1L,
					1L
				);
				prev = forSave;

				entityManager.persist(forSave);

				if(i % 50 == 0){
					entityManager.flush();
					entityManager.clear();
				}
			}
		});
	}

}
