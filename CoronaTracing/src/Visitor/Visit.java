package Visitor;

import java.time.Instant;

public class Visit {
	private int randomNumber;
	private String cateringFacility;
	private String userTokenSigned;
	private String userTokenUnsigned;
	private String businessNumber;
	private Instant beginTime;
	private Instant endTime;
	
	public Visit() {
		this.randomNumber = 0;
		this.cateringFacility = null;
	}
	
	public Visit(int randomNumber, String signed, String usigned, String cateringFacility) {
		this.randomNumber = randomNumber;
		this.cateringFacility = cateringFacility;
		this.userTokenSigned = signed;
		this.userTokenUnsigned = usigned;
	}

	public int getRandomNumber() {
		return randomNumber;
	}

	public void setRandomNumber(int randomNumber) {
		this.randomNumber = randomNumber;
	}

	public String getCateringFacility() {
		return cateringFacility;
	}

	public void setCateringFacility(String cateringFacility) {
		this.cateringFacility = cateringFacility;
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

	public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}

	public String toString() {
		return "Visit "+randomNumber+" Business number: "+this.businessNumber;
	}

}
