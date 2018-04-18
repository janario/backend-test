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
import me.janario.statistics.domain.StatisticsHelper;
import me.janario.transaction.domain.TransactionDto;
import me.janario.transaction.domain.TransactionResponseDto;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class StatisticsTests {
	@Autowired
	private StatisticsHelper statisticsHelper;
	@Autowired
	private TestRestTemplate restTemplate;

	@Before
	public void init() {
		statisticsHelper.cleanAll();
	}

	@Test
	public void emptyStatistics() {
		ResponseEntity<StatisticsDto> response = restTemplate.getForEntity("/statistics", StatisticsDto.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		StatisticsDto body = response.getBody();

		assertEquals(BigDecimal.ZERO, body.getSum());
		assertEquals(BigDecimal.ZERO, body.getAvg());
		assertNull(body.getMax());
		assertNull(body.getMin());
		assertEquals(0, body.getCount());
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
		assertEquals(BigDecimal.valueOf(55), body.getSum());
		assertEquals(new BigDecimal("5.50"), body.getAvg());
		assertEquals(BigDecimal.valueOf(10), body.getMax());
		assertEquals(BigDecimal.ONE, body.getMin());
		assertEquals(10, body.getCount());
	}

	@Test
	public void testExpiration() throws InterruptedException {
		for (int i = 11; i < 16; i++) {
			TransactionDto dto = new TransactionDto(BigDecimal.valueOf(i), Instant.now());
			restTemplate.postForEntity("/transactions", dto, TransactionResponseDto.class);
		}
		restTemplate.postForEntity("/transactions", new TransactionDto(BigDecimal.valueOf(10), Instant.now().minusSeconds(58)), TransactionResponseDto.class);
		restTemplate.postForEntity("/transactions", new TransactionDto(BigDecimal.valueOf(16), Instant.now().minusSeconds(58)), TransactionResponseDto.class);

		ResponseEntity<StatisticsDto> response = restTemplate.getForEntity("/statistics", StatisticsDto.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		StatisticsDto body = response.getBody();

		// values: 10, 11, 12, 13, 14, 15, 16
		assertEquals(BigDecimal.valueOf(91), body.getSum());
		assertEquals(new BigDecimal("13.00"), body.getAvg());
		assertEquals(BigDecimal.valueOf(16), body.getMax());
		assertEquals(BigDecimal.valueOf(10), body.getMin());
		assertEquals(7, body.getCount());

		//FIXME see other to wait for expiration
		Thread.sleep(2_100);
		response = restTemplate.getForEntity("/statistics", StatisticsDto.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		body = response.getBody();

		// values: 11, 12, 13, 14, 15
		assertEquals(BigDecimal.valueOf(65), body.getSum());
		assertEquals(new BigDecimal("13.00"), body.getAvg());
		assertEquals(BigDecimal.valueOf(15), body.getMax());
		assertEquals(BigDecimal.valueOf(11), body.getMin());
		assertEquals(5, body.getCount());
	}
}