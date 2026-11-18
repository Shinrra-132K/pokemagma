package co.edu.unbosque.pokemagma.dto;

import java.util.Objects;

import co.edu.unbosque.pokemagma.model.PlayerProfile.Gender;

/**
 * Datos de transferencia del perfil del entrenador.
 *
 * La relación con la cuenta se representa mediante su identificador.
 */
public class PlayerProfileDTO {
	/**
	 * Identificador del perfil
	 */
	private Long id;
	/**
	 * Identidficador de la cuenta asociada
	 */
	private Long userId;
	/**
	 * Genero seleccionado
	 */
	private Gender gender;
	/**
	 * Identificador de la operacion del entrenador
	 */
	private String trainerApparence;

	/**
	 * Constructor vacio
	 */
	public PlayerProfileDTO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor con todos los campos del perfil
	 * 
	 * @param id               identificador del perfil
	 * @param userId           identificador de la cuenta relacionada
	 * @param gender           genero seleccionado
	 * @param trainerApparence apariencia del entrenador
	 */
	public PlayerProfileDTO(Long id, Long userId, Gender gender, String trainerApparence) {
		super();
		this.id = id;
		this.userId = userId;
		this.gender = gender;
		this.trainerApparence = trainerApparence;
	}

	/** @return identificador del perfil */
	public Long getId() {
		return id;
	}

	/** @param id identificador del perfil */
	public void setId(Long id) {
		this.id = id;
	}

	/** @return ID de la cuenta relacionada */
	public Long getUserId() {
		return userId;
	}

	/** @param userId ID de la cuenta relacionada */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/** @return género seleccionado */
	public Gender getGender() {
		return gender;
	}

	/** @param gender género seleccionado */
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	/** @return apariencia del entrenador */
	public String getTrainerAppearance() {
		return trainerApparence;
	}

	/** @param trainerAppearance apariencia del entrenador */
	public void setTrainerAppearance(String trainerAppearance) {
		this.trainerApparence = trainerAppearance;
	}

	/**
	 * @return codigo has del DTO
	 */
	@Override
	public int hashCode() {
		return Objects.hash(gender, id, trainerApparence, userId);
	}

	/**
	 * Compara este DTO con otro por sus campos
	 * 
	 * @param obj objeto que se compara
	 * @return true si los campos coinciden
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerProfileDTO other = (PlayerProfileDTO) obj;
		return gender == other.gender && Objects.equals(id, other.id)
				&& Objects.equals(trainerApparence, other.trainerApparence) && Objects.equals(userId, other.userId);
	}

	/**
	 * @return representacion en texto del DTO
	 */
	@Override
	public String toString() {
		return "PlayerProfileDTO [id=" + id + ", userId=" + userId + ", gender=" + gender + ", trainerApparence="
				+ trainerApparence + "]";
	}

}
