package com.jobosint.problem;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ProblemService {

    private final ChatClient.Builder chatClientBuilder;
    private ChatClient chatClient;

    public ProblemService(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
        this.chatClient = chatClientBuilder
                .build();
    }


    public List<String> getAllProblems() {
        return List.of("problem 1", "problem 2", "problem 3");
    }

    public ProblemStepsResponse getProblemSteps(String problem) {

        String problemStepsUserMessage = """
                Given the following problem, please provide a list of steps to solve this problem.
                Only include the steps with no extra commentary and no formatting.
                Each step should be a plain text string.
                Here is the problem:
                {problem}
                """;

        var promptTemplate = new PromptTemplate(problemStepsUserMessage, Map.of(
                "problem", problem
        ));

        return chatClient.prompt(promptTemplate.create())
                .call()
                .entity(ProblemStepsResponse.class);
    }


    public ProblemVariationResponse getAllProblemVariations(String problem) {

        String problemVariationUserMessage = """
                Given the following problem, please provide a list of variations of this problem.
                Only include the variations with no extra commentary and no formatting.
                Each variation should be a plain text string.
                Here is the problem:
                {problem}
                """;

        var promptTemplate = new PromptTemplate(problemVariationUserMessage, Map.of(
                "problem", problem
        ));

        return chatClient.prompt(promptTemplate.create())
                .call()
                .entity(ProblemVariationResponse.class);
    }

    public ProblemPainPhraseResponse getProblemPainPhrases(String problem) {

        String problemPainPhrasesUserMessage = """
                Given the following problem, please provide a list of pain phrases for this problem.
                Only include the pain phrases with no extra commentary and no formatting.
                Order the pain phrases by severity from highest to lowest.
                Here is the problem:
                {problem}
                """;

        var promptTemplate = new PromptTemplate(problemPainPhrasesUserMessage, Map.of(
                "problem", problem
        ));

        return chatClient.prompt(promptTemplate.create())
                .call()
                .entity(ProblemPainPhraseResponse.class);
    }

    public ExistingSolutionsSummary getExistingSolutions(String problem) {

        String existingSolutionsUserMessage = """
                Given the following problem, please provide a list of at least ten existing software solutions for this problem.
                A software solution may include GitHub repositories, libraries, frameworks, websites, mobile apps, chrome extensions and Saas businesses.
                For each solution, provide what type of solution it is and at most five sources that provide direct evidence of the data you provide about the solution.
                Only include the existing solutions with no extra commentary and no formatting.
                Here is the problem:
                {problem}
                """;

        var promptTemplate = new PromptTemplate(existingSolutionsUserMessage, Map.of(
                "problem", problem
        ));

        return chatClient.prompt(promptTemplate.create()).call().entity(ExistingSolutionsSummary.class);
    }

    public CommunitySummary getCommunities(String topic) {

        String communityUserMessage = """
                Given the following topic, please provide a list of the most active online communities.
                Each community may be a subreddit, forum, or other online community.
                Provide at least ten communities sorted by number of subscribers and activity.
                For each community URL, make sure the URL is valid and exists - otherwise I'll charge you a severe penalty.
                Only include the communities with no extra commentary and no formatting.
                Here is the topic:
                {topic}
                """;

        var promptTemplate = new PromptTemplate(communityUserMessage, Map.of(
                "topic", topic
        ));

        return chatClient.prompt(promptTemplate.create()).call().entity(CommunitySummary.class);
    }

    public RecommendedQueriesResponse getRecommendedQueries(String topic) {

        String recommendedQueriesUserMessage = """
                Given the following topic, please provide a list of search queries to find additional information about the topic.
                Only include the queries with no extra commentary and no formatting.
                Here is the topic:
                {topic}
                """;

        var promptTemplate = new PromptTemplate(recommendedQueriesUserMessage, Map.of(
                "topic", topic
        ));

        return chatClient.prompt(promptTemplate.create()).call().entity(RecommendedQueriesResponse.class);
    }

    public ProblemSummary getProblemSummary(String problem) {
        // Create CompletableFuture for each API call
        CompletableFuture<ProblemVariationResponse> variationsFuture =
                CompletableFuture.supplyAsync(() -> getAllProblemVariations(problem));

        CompletableFuture<ProblemStepsResponse> stepsFuture =
                CompletableFuture.supplyAsync(() -> getProblemSteps(problem));

        CompletableFuture<ProblemPainPhraseResponse> painPhrasesFuture =
                CompletableFuture.supplyAsync(() -> getProblemPainPhrases(problem));

        CompletableFuture<ExistingSolutionsSummary> existingSolutionsFuture =
                CompletableFuture.supplyAsync(() -> getExistingSolutions(problem));

        CompletableFuture<CommunitySummary> communitiesFuture =
                CompletableFuture.supplyAsync(() -> getCommunities(problem));

        CompletableFuture<RecommendedQueriesResponse> queriesFuture =
                CompletableFuture.supplyAsync(() -> getRecommendedQueries(problem));

        // Wait for all futures to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                variationsFuture, stepsFuture, painPhrasesFuture, existingSolutionsFuture, queriesFuture
        );

        // Get the results once all futures are complete
        try {
            allFutures.join();
            return new ProblemSummary(
                    stepsFuture.get(),
                    queriesFuture.get(),
                    variationsFuture.get(),
                    painPhrasesFuture.get(),
                    existingSolutionsFuture.get(),
                    communitiesFuture.get()
            );
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error retrieving problem summary data", e);
        }
    }

    public PainSummaryResponse getPainSummary(PainSummaryRequest painSummaryRequest) {
        String painSummaryUserMessage = """
                Given the following pain summary request, provide a pain summary response.
                
                The `summary` string should be one page in markdown format.
                
                Include at least 10 common complaints related to the problem.
                
                Include at least 3 recommendations that a sole software engineer can do on his own to address the problem.
                
                Only include the response with no extra commentary and no formatting.
                
                Here is the pain summary request:
                {request}
                """;

        var promptTemplate = new PromptTemplate(painSummaryUserMessage, Map.of(
                "request", painSummaryRequest
        ));

        return chatClient.prompt(promptTemplate.create()).call().entity(PainSummaryResponse.class);
    }
}
