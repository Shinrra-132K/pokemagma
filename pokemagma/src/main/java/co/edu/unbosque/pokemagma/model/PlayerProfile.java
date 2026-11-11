package co.edu.unbosque.pokemagma.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Representa el perfil de jugador asociado a una cuenta de usuario
 * 
 * Guarda la seccion de genero y la apariencia del entrenador
 * El mapa y el movimiento pertenecen al fronted y no persisten en el back
 */
@Entity
@Table(name = "player_profile")
public class PlayerProfile {
	
	/**
	 * Generos disponibles para asociar el aspecto inicial
	 */
	public enum Gender {
		/**
		 * Apariencia masculina y femenina
		 */
		MALE, FEMALE
	}
	
	/**
	 * Id del perfil
	 */
	@Id
	private Long id;
	
	/**
	 * Cuenta a la que pertenece el perfil del jugador
	 */
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@MapsId
	@JoinColumn(name = "user_id")
	private User user;
	
	/**
	 * Genero seleccionado para el perfil del entrenador
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Gender gender;
	
	/**
	 * Identificador de la apariencia del entrenador
	 */
	@Column(nullable = false)
	private String trainerApparance;
	
	
	public PlayerProfile() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Crea el perfil de un jugador
	 * 
	 * @param user cuenta asociada al perfil
	 * @param gender genero seleccionado
	 * @param trainerApparance identificador de la apariencia inicial
	 */
	public PlayerProfile(User user, Gender gender, String trainerApparance) {
		super();
		this.user = user;
		this.gender = gender;
		this.trainerApparance = trainerApparance;
	}

	/**
	 * 
	 * Getters y setters de las variables
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getTrainerApparance() {
		return trainerApparance;
	}

	public void setTrainerApparance(String trainerApparance) {
		this.trainerApparance = trainerApparance;
	}
	
	
	/**
	 * Devuelve un hash estable durante la asignacion del ID
	 * 
	 * @return codigo hash de la clase
	 */
	@Override
	public int hashCode() {
		return Objects.hash(gender, id, trainerApparance, user);
	}

	/**
	 * Compara perfiles por su ID establecido
	 * 
	 * @param obj objeto que se compara
	 * @return true si ambos perfiles tienen el mismo ID
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerProfile other = (PlayerProfile) obj;
		return gender == other.gender && Objects.equals(id, other.id)
				&& Objects.equals(trainerApparance, other.trainerApparance) && Objects.equals(user, other.user);
	}

	/**
	 * Devuelve una representacion textual del perfil
	 * 
	 * @return texto con los datos del perfil
	 */
	@Override
	public String toString() {
		return "PlayerProfile [id=" + id + ", user=" + user + ", gender=" + gender + ", trainerApparance="
				+ trainerApparance + "]";
	}
	
	
	
	
	
	
	
	
	

}
