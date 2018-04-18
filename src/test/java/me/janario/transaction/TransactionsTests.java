package me.janario.transaction;

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

import me.janario.transaction.domain.TransactionDto;
import me.janario.transaction.domain.TransactionResponseDto;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TransactionsTests {
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void createTransaction() {
		TransactionDto dto = new TransactionDto(new BigDecimal("10.1"), Instant.now());
		createAssertResponse(dto);
	}

	@Test
	public void oldValidTransaction() {
		TransactionDto dto = new TransactionDto(BigDecimal.TEN, Instant.now().minusSeconds(30));
		createAssertResponse(dto);
	}

	@Test
	public void oldTransaction() {
		TransactionDto dto = new TransactionDto(new BigDecimal("12.3"), Instant.now().minusSeconds(61));
		assertNoContent(dto);
	}

	@Test
	public void exampleTest() {
		TransactionDto dto = new TransactionDto(BigDecimal.ONE, Instant.ofEpochMilli(1478192204000L));
		assertNoContent(dto);
	}

	@Test
	public void invalidDataAmount() {
		assertInvalidData(new TransactionDto(null, Instant.now()));
	}

	@Test
	public void invalidDataTimestamp() {
		assertInvalidData(new TransactionDto(BigDecimal.TEN, null));
	}

	@Test
	public void invalidData() {
		assertInvalidData(new TransactionDto());
	}

	private void assertInvalidData(TransactionDto dto) {
		ResponseEntity<TransactionResponseDto> response = restTemplate.postForEntity("/transactions", dto, TransactionResponseDto.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	private void createAssertResponse(TransactionDto dto) {
		ResponseEntity<Void> response = restTemplate.postForEntity("/transactions", dto, Void.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());

		TransactionResponseDto created = restTemplate.getForObject(response.getHeaders().getLocation(), TransactionResponseDto.class);
		assertEquals(dto.getAmount(), created.getAmount());
		assertEquals(dto.getTimestamp(), created.getTime());
	}

	private void assertNoContent(TransactionDto dto) {
		ResponseEntity<String> response = restTemplate.postForEntity("/transactions", dto, String.class);
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}
}