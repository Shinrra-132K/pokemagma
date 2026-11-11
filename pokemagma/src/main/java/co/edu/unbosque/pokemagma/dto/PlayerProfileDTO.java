package co.edu.unbosque.pokemagma.dto;

import java.util.Objects;

import co.edu.unbosque.pokemagma.model.PlayerProfile.Gender;

/**
 * DTO para transferir los datos del perfil del entrenador
 * 
 * la relacion con el ususario se representa mediante su ID
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
	 * @param id identificador del perfil
	 * @param userId identificador de la cuenta relacionada
	 * @param gender genero seleccionado
	 * @param trainerApparence apariencia del entrenador
	 */
	public PlayerProfileDTO(Long id, Long userId, Gender gender, String trainerApparence) {
		super();
		this.id = id;
		this.userId = userId;
		this.gender = gender;
		this.trainerApparence = trainerApparence;
	}


	/**
	 * 
	 * getters y setters de los campos
	 */
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public Gender getGender() {
		return gender;
	}


	public void setGender(Gender gender) {
		this.gender = gender;
	}


	public String getTrainerApparence() {
		return trainerApparence;
	}


	public void setTrainerApparence(String trainerApparence) {
		this.trainerApparence = trainerApparence;
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
