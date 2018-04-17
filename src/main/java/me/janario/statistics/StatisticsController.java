package me.janario.statistics;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class StatisticsController {
	private final AtomicInteger ids = new AtomicInteger();
	private final Map<Integer, TransactionDto> transactions = new ConcurrentHashMap<>();

	@PostMapping("/transactions")
	public ResponseEntity<Object> createTrasaction(@RequestBody TransactionDto dto) {
		if (dto.getTimestamp().isBefore(Instant.now().minusSeconds(60))) {
			return ResponseEntity.noContent().build();
		}

		int id;
		transactions.put(id = ids.incrementAndGet(), dto);
		return ResponseEntity.created(
				ServletUriComponentsBuilder.fromCurrentContextPath()
						.replacePath("/transaction/" + id).build().toUri())
				.body(dto);
	}

	@GetMapping("/statistics")
	public String statistics() {
		//TODO
		return "test";
	}


	@GetMapping("/transaction/{id}")
	public ResponseEntity<Object> transaction(@PathVariable Integer id) {
		TransactionDto dto = transactions.get(id);
		if (dto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(dto);
	}
}
