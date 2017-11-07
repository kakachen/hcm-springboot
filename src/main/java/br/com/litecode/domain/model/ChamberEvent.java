package br.com.litecode.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static br.com.litecode.domain.model.Session.SessionStatus;
import static java.time.temporal.ChronoUnit.SECONDS;

@Entity
@Getter
@Setter
public class ChamberEvent implements Comparable<ChamberEvent>, Serializable {
	@AllArgsConstructor
	@Getter
	public enum EventType {
		START(SessionStatus.COMPRESSING),
		WEAR_MASK(SessionStatus.O2_ON),
		REMOVE_MASK(SessionStatus.O2_OFF),
		SHUTDOWN(SessionStatus.SHUTTING_DOWN),
		COMPLETION(SessionStatus.FINISHED);

		private SessionStatus sessionStatus;

		private static final EventType[] values = values();

		public EventType next() {
			return values[(this.ordinal() + 1) % values.length];
		}
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer eventId;

	private Integer timeout;

	@Enumerated(value = EnumType.STRING)
	private EventType eventType;

	public String getDuration() {
		Duration duration = Duration.of(timeout, SECONDS);
		return LocalTime.MIDNIGHT.plus(duration).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}

	@Override
	public int compareTo(ChamberEvent chamberEvent) {
		return timeout.compareTo(chamberEvent.getTimeout());
	}

	@Override
	public String toString() {
		return eventType.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ChamberEvent that = (ChamberEvent) o;

		return eventId.equals(that.eventId);
	}

	@Override
	public int hashCode() {
		return eventId.hashCode();
	}
}
