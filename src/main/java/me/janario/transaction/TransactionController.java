package me.janario.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import me.janario.transaction.domain.TransactionDto;
import me.janario.transaction.domain.TransactionResponseDto;
import me.janario.transaction.domain.TransactionService;

@RestController
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@PostMapping("/transactions")
	public ResponseEntity<TransactionResponseDto> createTrasaction(@RequestBody TransactionDto dto) {
		if (dto.isOlderThan60Seconds()) {
			return ResponseEntity.noContent().build();
		}

		int id = transactionService.register(dto);
		return ResponseEntity.created(
				ServletUriComponentsBuilder.fromCurrentContextPath()
						.replacePath("/transaction/" + id).build().toUri())
				.body(dto.toResponse());
	}

	@GetMapping("/transaction/{id}")
	public ResponseEntity<Object> transaction(@PathVariable Integer id) {
		TransactionDto dto = transactionService.findById(id);
		if (dto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(dto);
	}
}
