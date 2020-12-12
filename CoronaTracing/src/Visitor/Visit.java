package Visitor;

import java.time.Instant;

public class Visit {
	private int randomNumber;
	private String cateringFacilityToken;
	private String userTokenSigned;
	private String userTokenUnsigned;
	private String businessNumber;
	private Instant beginTime;

	public Visit() {
		this.randomNumber = 0;
		this.cateringFacilityToken = null;
	}

	public Visit(int randomNumber, String signed, String usigned, String cateringFacility) {
		this.randomNumber = randomNumber;
		this.cateringFacilityToken = cateringFacility;
		this.userTokenSigned = signed;
		this.userTokenUnsigned = usigned;
	}

	public int getRandomNumber() {
		return randomNumber;
	}



	public void setRandomNumber(int randomNumber) {
		this.randomNumber = randomNumber;
	}

	public String getCateringFacilityToken() {
		return cateringFacilityToken;
	}

	public void setCateringFacilityToken(String cateringFacility) {
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

	public String getBusinessNumber() {
		return businessNumber;
	}

	public void setBusinessNumber(String businessNumber) {
		this.businessNumber = businessNumber;
	}

	public Instant getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Instant beginTime) {
		this.beginTime = beginTime;
	}

	public String toString() {
		return "Visit " + this.randomNumber + " Business number: " + this.businessNumber;
	}

	public String toExtendedString() {
		return this.randomNumber + "_" + this.userTokenSigned + "_" + this.userTokenUnsigned + "_"
				+ this.cateringFacilityToken + "_" + this.beginTime + "_" +this.businessNumber;
	}

}
