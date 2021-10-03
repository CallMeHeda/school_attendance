package org.isfce.pid.service.dto;

import java.util.ArrayList;
import java.util.List;

import org.isfce.pid.model.Presence;

import lombok.Data;

@Data
public class PresenceDto {

	private List<Presence> presencesDto;

	public PresenceDto() {
	        this.presencesDto = new ArrayList<>();
	    }

	    public PresenceDto(List<Presence> presences) {
	        this.presencesDto = presences;
	    }

	    public List<Presence> getPresencesDto() {
	        return presencesDto;
	    }

	    public void setPresencesDto(List<Presence> presences) {
	        this.presencesDto = presences;
	    }
	    
	    public static PresenceDto toDto(List<Presence> presences) {
			
			PresenceDto pDto = new PresenceDto(presences);
			return pDto;
		}
	    
	    public void addPresence(Presence presence) {
	        this.presencesDto.add(presence);
	    }
}
