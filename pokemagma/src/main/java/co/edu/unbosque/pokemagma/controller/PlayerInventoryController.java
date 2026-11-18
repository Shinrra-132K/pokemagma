package co.edu.unbosque.pokemagma.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.pokemagma.dto.InventoryItemDTO;
import co.edu.unbosque.pokemagma.exception.ResourceNotFoundException;
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.service.InventoryItemService;
import co.edu.unbosque.pokemagma.service.UserService;

/**
 * Controlador de consulta del inventario del jugador autenticado.
 */
@RestController
@RequestMapping("/player/inventory")
public class PlayerInventoryController {

	private final InventoryItemService inventoryService;
	private final UserService userService;

	/**
	 * Crea el controlador del inventario.
	 *
	 * @param inventoryService servicio de consulta del inventario
	 */
	public PlayerInventoryController(InventoryItemService inventoryService, UserService userService) {
		super();
		this.inventoryService = inventoryService;
		this.userService = userService;
	}
	

	/**
	 * Devuelve los objetos del usuario autenticado.
	 *
	 * @param principal usuario obtenido del JWT
	 * @return objetos del jugador o 204 si el inventario está vacío
	 * @throws ResourceNotFoundException 
	 */
	@GetMapping("/getall")
	public ResponseEntity<List<InventoryItemDTO>> getAll(Principal principal) throws ResourceNotFoundException {
		Long playerId = userService.findIdByUsername(principal.getName());
		List<InventoryItemDTO> items = inventoryService.getByPlayerId(playerId);

		if (items.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(items);
	}


}
