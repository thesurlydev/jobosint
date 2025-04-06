package com.jobosint.problem;

public record ProblemSummary(ProblemStepsResponse steps,
                             RecommendedQueriesResponse queries,
                             ProblemVariationResponse variations,
                             ProblemPainPhraseResponse painPhrases,
                             ExistingSolutionsSummary existingSolutions,
                             CommunitySummary communitySummary) {
}
