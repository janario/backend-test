package me.janario.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import me.janario.statistics.domain.StatisticsDto;
import me.janario.transaction.domain.TransactionDto;
import me.janario.transaction.domain.TransactionHelper;
import me.janario.transaction.domain.TransactionResponseDto;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class StatisticsTests {
	@Autowired
	private TransactionHelper transactionHelper;
	@Autowired
	private TestRestTemplate restTemplate;

	@Before
	public void init() {
		transactionHelper.cleanAll();
	}

	@Test
	public void emptyStatistics() {
		ResponseEntity<StatisticsDto> response = restTemplate.getForEntity("/statistics", StatisticsDto.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		StatisticsDto body = response.getBody();

		assertEquals(body.getSum(), BigDecimal.ZERO);
		assertEquals(body.getAvg(), BigDecimal.ZERO);
		assertNull(body.getMax());
		assertNull(body.getMin());
		assertEquals(body.getCount(), 0);
	}

	@Test
	public void testStatistics() {
		for (int i = 1; i < 11; i++) {
			TransactionDto dto = new TransactionDto(BigDecimal.valueOf(i), Instant.now());
			restTemplate.postForEntity("/transactions", dto, TransactionResponseDto.class);
		}

		ResponseEntity<StatisticsDto> response = restTemplate.getForEntity("/statistics", StatisticsDto.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		StatisticsDto body = response.getBody();

		// values: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
		assertEquals(body.getSum(), BigDecimal.valueOf(55));
		assertEquals(body.getAvg(), new BigDecimal("5.50"));
		assertEquals(body.getMax(), BigDecimal.valueOf(10));
		assertEquals(body.getMin(), BigDecimal.ONE);
		assertEquals(body.getCount(), 10);
	}
}