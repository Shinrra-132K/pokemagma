package co.edu.unbosque.pokemagma.dto;

import java.util.Objects;

import co.edu.unbosque.pokemagma.model.PlayerProfile.Gender;

public class PlayerProfileDTO {
	
	private Long id;
	private Long userId;
	private Gender gender;
	private String trainerApparence;
	
	
	public PlayerProfileDTO() {
		// TODO Auto-generated constructor stub
	}


	public PlayerProfileDTO(Long id, Long userId, Gender gender, String trainerApparence) {
		super();
		this.id = id;
		this.userId = userId;
		this.gender = gender;
		this.trainerApparence = trainerApparence;
	}


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


	@Override
	public int hashCode() {
		return Objects.hash(gender, id, trainerApparence, userId);
	}


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


	@Override
	public String toString() {
		return "PlayerProfileDTO [id=" + id + ", userId=" + userId + ", gender=" + gender + ", trainerApparence="
				+ trainerApparence + "]";
	}
	
	

}
