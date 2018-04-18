package me.janario.transaction;

import javax.validation.Valid;

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
	public ResponseEntity<Void> createTrasaction(@RequestBody @Valid TransactionDto dto) {
		TransactionResponseDto response = transactionService.register(dto);

		if (response.isOlderThan60Seconds()) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.created(
					ServletUriComponentsBuilder.fromCurrentContextPath()
							.replacePath("/transaction/" + response.getId()).build().toUri())
					.build();
		}
	}

	@GetMapping("/transaction/{id}")
	public ResponseEntity<TransactionResponseDto> transaction(@PathVariable Long id) {
		TransactionResponseDto dto = transactionService.findById(id);
		if (dto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(dto);
	}
}
