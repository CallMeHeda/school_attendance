package org.isfce.pid.controller;

//Map cette exception sur une erreur HTTP 404 
//@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Elément non trouvé !")
public class NotExistException extends RuntimeException {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	//identifiant de l'objet recherche
	private String code;

	public NotExistException(String message,String code) {
		super(message);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
