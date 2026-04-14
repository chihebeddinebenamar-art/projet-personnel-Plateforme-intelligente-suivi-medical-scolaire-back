package tn.educanet.pfe.api.dto;

import jakarta.validation.constraints.NotBlank;

public class CarnetNumeriqueUploadRequest {

	/**
	 * Image encodée en base64, avec ou sans préfixe {@code data:image/...;base64,}.
	 */
	@NotBlank
	private String image;

	public CarnetNumeriqueUploadRequest() {
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
