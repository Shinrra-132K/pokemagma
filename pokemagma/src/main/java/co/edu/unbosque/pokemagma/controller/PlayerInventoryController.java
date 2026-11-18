package co.edu.unbosque.pokemagma.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.pokemagma.dto.InventoryItemDTO;
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.service.InventoryItemService;

/**
 * Controlador de consulta del inventario del jugador autenticado.
 */
@RestController
@RequestMapping("/player/inventory")
public class PlayerInventoryController {

	private final InventoryItemService inventoryService;

	/**
	 * Crea el controlador del inventario.
	 *
	 * @param inventoryService servicio de consulta del inventario
	 */
	public PlayerInventoryController(InventoryItemService inventoryService) {
		this.inventoryService = inventoryService;
	}

	/**
	 * Devuelve los objetos del usuario autenticado.
	 *
	 * @param currentUser usuario obtenido del JWT
	 * @return objetos del jugador o 204 si el inventario está vacío
	 */
	@GetMapping("/getall")
	public ResponseEntity<List<InventoryItemDTO>> getAll(@AuthenticationPrincipal User currentUser) {

		List<InventoryItemDTO> items = inventoryService.getByPlayerId(currentUser.getId());

		if (items.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(items);
	}

}
