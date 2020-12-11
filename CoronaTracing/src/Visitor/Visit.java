package Visitor;

import java.time.Instant;

public class Visit {
	private int randomNumber;
	private String cateringFacilityToken;
	private String userTokenSigned;
	private String userTokenUnsigned;
	private Instant instant;
	
	public Visit() {
		this.randomNumber = 0;
		this.cateringFacilityToken = null;
	}
	
	public Visit(int randomNumber, String signed, String usigned, String cateringFacility, Instant instant) {
		this.randomNumber = randomNumber;
		this.cateringFacilityToken = cateringFacility;
		this.userTokenSigned = signed;
		this.userTokenUnsigned = usigned;
		this.instant = instant;
	}

	public int getRandomNumber() {
		return randomNumber;
	}

	public Instant getInstant() {
		return instant;
	}

	public void setInstant(Instant instant) {
		this.instant = instant;
	}


	public void setRandomNumber(int randomNumber) {
		this.randomNumber = randomNumber;
	}

	public String getCateringFacility() {
		return cateringFacilityToken;
	}

	public void setCateringFacility(String cateringFacility) {
		this.cateringFacilityToken = cateringFacility;
	}

	public String getUserTokenSigned() {
		return userTokenSigned;
	}

	public void setUserTokenSigned(String userTokenSigned) {
		this.userTokenSigned = userTokenSigned;
	}

	public String getUserTokenUnsigned() {
		return userTokenUnsigned;
	}

	public void setUserTokenUnsigned(String userTokenUnsigned) {
		this.userTokenUnsigned = userTokenUnsigned;
	}
}
