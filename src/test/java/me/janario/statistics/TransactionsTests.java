package me.janario.statistics;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TransactionsTests {
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void createTransaction() {
		TransactionDto dto = new TransactionDto(new BigDecimal("10.1"), Instant.now());

		ResponseEntity<String> response = restTemplate.postForEntity("/transactions", dto, String.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());

		TransactionDto created = restTemplate.getForObject(response.getHeaders().getLocation(), TransactionDto.class);
		assertEquals(dto.getAmount(), created.getAmount());
		assertEquals(dto.getTimestamp(), created.getTimestamp());
	}

	@Test
	public void oldValidTransaction() {
		TransactionDto dto = new TransactionDto(BigDecimal.TEN, Instant.now().minusSeconds(30));

		ResponseEntity<String> response = restTemplate.postForEntity("/transactions", dto, String.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());

		TransactionDto created = restTemplate.getForObject(response.getHeaders().getLocation(), TransactionDto.class);
		assertEquals(dto.getAmount(), created.getAmount());
		assertEquals(dto.getTimestamp(), created.getTimestamp());
	}

	@Test
	public void oldTransaction() {
		TransactionDto dto = new TransactionDto(new BigDecimal("12.3"), Instant.now().minusSeconds(61));

		ResponseEntity<String> response = restTemplate.postForEntity("/transactions", dto, String.class);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	public void exampleTest() {
		TransactionDto dto = new TransactionDto(BigDecimal.ONE, Instant.ofEpochMilli(1478192204000L));

		ResponseEntity<String> response = restTemplate.postForEntity("/transactions", dto, String.class);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}
}