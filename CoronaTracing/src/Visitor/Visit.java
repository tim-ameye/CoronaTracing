package Visitor;

public class Visit {
	private int randomNumber;
	private String cateringFacility;
	private String userTokenSigned;
	private String userTokenUnsigned;
	
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
}
