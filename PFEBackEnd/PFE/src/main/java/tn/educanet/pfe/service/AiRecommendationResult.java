package tn.educanet.pfe.service;

public record AiRecommendationResult(
		boolean ok,
		String message,
		String patientSummary,
		String statisticalSummary,
		String businessRules,
		String llmPrompt,
		String recommendations,
		String vaccinationPlan,
		String preventionAdvice) {

	public static AiRecommendationResult fail(String message) {
		return new AiRecommendationResult(false, message, null, null, null, null, null, null, null);
	}
}
