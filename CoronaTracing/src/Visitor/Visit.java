package Visitor;

public class Visit {
	private int randomNumber;
	private String cateringFacility;
	private byte[] userToken;
	
	public Visit() {
		this.randomNumber = 0;
		this.cateringFacility = null;
		this.userToken = null;
	}
	
	public Visit(int randomNumber, byte[] userToken, String cateringFacility) {
		this.randomNumber = randomNumber;
		this.cateringFacility = cateringFacility;
		this.userToken = userToken;
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

	public byte[] getUserToken() {
		return userToken;
	}

	public void setUserToken(byte[] userToken) {
		this.userToken = userToken;
	}
}
