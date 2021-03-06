package br.com.litecode.service.timer;

import br.com.litecode.domain.model.ChamberEvent;
import br.com.litecode.domain.model.PatientSession;
import br.com.litecode.domain.model.Session;
import br.com.litecode.domain.model.Session.SessionStatus;
import br.com.litecode.domain.model.User;
import br.com.litecode.domain.repository.SessionRepository;
import br.com.litecode.domain.repository.UserRepository;
import br.com.litecode.service.push.PushChannel;
import br.com.litecode.service.push.PushService;
import br.com.litecode.service.push.message.NotificationMessage;
import br.com.litecode.service.push.message.ProgressMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChamberSessionTimer implements SessionTimer {
	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PushService pushService;

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	@Getter
	private TimeTicker<Session> sessionTimeTicker;

	@Override
	public void startSession(Session session) {
		List<ChamberEvent> chamberEvents = session.getChamber().getChamberEvents();

		for (ChamberEvent chamberEvent : chamberEvents) {
			if (session.getExecutionMetadata().getElapsedTime() > chamberEvent.getTimeout()) {
				continue;
			}

			long delay = chamberEvent.getTimeout() - session.getExecutionMetadata().getElapsedTime();
			sessionTimeTicker.register(session, () -> sessionTimeout(session, chamberEvent), delay);
			log.debug("Chamber event {} scheduled: {}s", chamberEvent, delay);
		}

		sessionTimeTicker.start(session);
	}

	private void sessionTimeout(Session session, ChamberEvent chamberEvent) {
		session = sessionRepository.findOne(session.getSessionId());
		session.setStatus(chamberEvent.getEventType().getSessionStatus());
		session.getExecutionMetadata().setCurrentEvent(session.getStatus() != SessionStatus.FINISHED ? chamberEvent : null);
 		session.updateProgress();
		sessionRepository.save(session);

		if (session.getStatus() == SessionStatus.FINISHED) {
			sessionTimeTicker.stop(session);
            onSessionComplete(session);
		}

		User user = userRepository.findByUsername(session.getExecutionMetadata().getStartedBy());
		pushService.publish(PushChannel.NOTIFY, NotificationMessage.create(session, chamberEvent.getEventType()), user);
	}

	private void onSessionComplete(Session session) {
        Cache patientCache = cacheManager.getCache("patient");
        List<Integer> patientIds = session.getPatientSessions().stream().map(PatientSession::getPatientId).collect(Collectors.toList());
        patientIds.forEach(patientCache::evict);
    }

	@Override
	public void stopSession(Session session) {
		sessionTimeTicker.stop(session);
	}

	@Scheduled(fixedRate = 1000)
	private void clockTimeout() {
		for (Session session : sessionTimeTicker.getActiveListeners()) {
			session.updateProgress();
			pushService.publish(PushChannel.PROGRESS, ProgressMessage.create(session), null);
		}
	}
}

