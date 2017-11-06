package br.com.litecode.domain.model;

import com.google.common.base.Joiner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
public class User {
	public enum Role { DEV, ADMIN, USER }
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;

	@Enumerated(EnumType.STRING)
	private Role role;

	private String username;
	private String password;
	private String name;
	private boolean active;
	private Instant lastAccess;
	private Instant creationDate;
	private String sessionId;
	private String lastAccessLocation;
	private String timeZone;

	@Embedded
	private UserSettings userSettings;
	
    public User() {
    	active = true;
    	creationDate = Instant.now();
		userSettings = new UserSettings();
    }

    public String getLastAccessLocationFormatted() {
		if (lastAccessLocation == null) {
			return "";
		}

		JsonParser jsonParser = new JsonParser();
		JsonObject locationJson = jsonParser.parse(lastAccessLocation).getAsJsonObject();

		String ip = locationJson.get("ip") == null ? null : "IP: " + locationJson.get("ip").getAsString();
		String city = locationJson.get("city") == null ? null : "City: " + locationJson.get("city").getAsString();
		String region = locationJson.get("region") == null ? null : "Region: " + locationJson.get("region").getAsString();
		String country = locationJson.get("country") == null ? null : "Country: " + locationJson.get("country").getAsString();
		String location = locationJson.get("loc") == null ? null : "Location: " + locationJson.get("loc").getAsString();

		return Joiner.on("<br/>").skipNulls().join(ip, city, region, country, location);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		return username.equals(other.username);
	}

	@Override
	public String toString() {
		return "[" + userId + "] " + username;
	}
}