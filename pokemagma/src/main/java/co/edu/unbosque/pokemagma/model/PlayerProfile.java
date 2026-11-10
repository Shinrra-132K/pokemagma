package co.edu.unbosque.pokemagma.model;

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

@Entity
@Table(name = "player_profile")
public class PlayerProfile {
	
	public enum Gender {
		MALE, FEMALE
	}
	
	@Id
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@MapsId
	@JoinColumn(name = "user_id")
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Gender gender;
	
	@Column(nullable = false)
	private String trainerApparance;
	
	
	public PlayerProfile() {
		// TODO Auto-generated constructor stub
	}

	public PlayerProfile(Long id, User user, Gender gender, String trainerApparance) {
		super();
		this.id = id;
		this.user = user;
		this.gender = gender;
		this.trainerApparance = trainerApparance;
	}

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

	@Override
	public String toString() {
		return "PlayerProfile [id=" + id + ", user=" + user + ", gender=" + gender + ", trainerApparance="
				+ trainerApparance + "]";
	}
	
	
	
	
	
	
	
	
	

}
