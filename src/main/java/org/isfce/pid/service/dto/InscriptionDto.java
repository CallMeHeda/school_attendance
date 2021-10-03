package org.isfce.pid.service.dto;

import java.util.ArrayList;
import java.util.List;

import org.isfce.pid.model.Inscription;

import lombok.Data;

@Data
public class InscriptionDto {

	private List<Inscription> inscriptionsDto;

	public InscriptionDto() {
	        this.inscriptionsDto = new ArrayList<>();
	    }

	    public InscriptionDto(List<Inscription> inscriptions) {
	        this.inscriptionsDto = inscriptions;
	    }
	    
	    public static InscriptionDto toDto(List<Inscription> inscriptions) {
			
			InscriptionDto iDto = new InscriptionDto(inscriptions);
			return iDto;
		}
}
