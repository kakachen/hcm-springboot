package br.com.litecode.controller;

import br.com.litecode.domain.repository.ChartsRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.faces.view.ViewScoped;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@ViewScoped
@Component
@Cacheable(cacheNames = "chart", key = "#root.methodName")
public class ChartsController {
	@Autowired
	private ChartsRepository chartsRepository;

	public String getMonthlySessionsModel() {
		return loadChartData(chartsRepository::findMonthlySessions, "Mês", "Sessōes");
	}

	public String getSessionsPerChamberModel() {
		return loadChartData(chartsRepository::findSessionsPerChamber, "Câmara", "Sessōes");
	}

	public String getSessionsPerHealthInsuranceModel() {
		return loadChartData(chartsRepository::findSessionsPerHealthInsurance, "Plano de saúde", "Sessōes");
	}

	public String getPresencesPerMonthModel() {
		return loadChartData(chartsRepository::findMonthlyPresences, "Mês", "Presenças");
	}

	public String getPatientsPerMedicalIndicationModel() {
		return loadChartData(chartsRepository::findPatientsPerMedicalIndication, "Indicação médica", "Pacientes");
	}

	public String getPatientsPerConsultationReasonModel() {
		return loadChartData(chartsRepository::findPatientsPerConsultationReason, "Motivo consulta", "Pacientes");
	}

	public String getMonthlyNewPatients() {
		return loadChartData(chartsRepository::findMonthlyNewPatients, "Mês", "Pacientes");
	}

	@Cacheable(cacheNames = "chart", key = "#patientId")
	public String getPatientAttendance(Integer patientId) {
		return loadChartData(chartsRepository::findPatientAttendance, patientId);
	}

	private String loadChartData(Supplier<List<Object[]>> dao, String... headers) {
		List<Object[]> results = dao.get();
		return getJsonResults(results, headers);
	}

	private String loadChartData(Function<Integer, List<Object[]>> dao, Integer id, String... headers) {
		List<Object[]> results = dao.apply(id);
		return getJsonResults(results, headers);
	}

	private String getJsonResults(List<Object[]> results, String... headers) {
		if (results.isEmpty()) {
			return null;
		}

		if (headers.length > 0) {
			results.add(0, headers);
		}
		return new Gson().toJson(results);
	}
}
