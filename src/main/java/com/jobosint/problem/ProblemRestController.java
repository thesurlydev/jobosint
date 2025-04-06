package com.jobosint.problem;

import com.jobosint.brave.BraveMcpClient;
import com.jobosint.brave.BraveSearchResponse;
import com.jobosint.fetch.FetchMcpClient;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ProblemRestController {

    private final ProblemService problemService;
    private final BraveMcpClient braveMcpClient;
    private final FetchMcpClient fetchMcpClient;

    @GetMapping("/problems")
    @Operation(summary = "Get all problems")
    public Iterable<String> getProblems() {
        return problemService.getAllProblems();
    }

    @GetMapping("/variations")
    @Operation(summary = "Get problem summary")
    public ProblemSummary getProblemSummary(@RequestParam String problem) {
        return problemService.getProblemSummary(problem);
    }

    @GetMapping("/pain")
    @Operation(summary = "Given a problem, list of search queries, and a list of seed URLs, determine what common complaints are associated with the problem.")
    public PainSummaryResponse getPainSummary(@RequestParam String problem) {
        ProblemSummary problemSummary = problemService.getProblemSummary(problem);
        PainSummaryRequest painSummaryRequest = new PainSummaryRequest(problem, problemSummary.queries().queries(), problemSummary.communitySummary().communities());
        return problemService.getPainSummary(painSummaryRequest);
    }

    @GetMapping("/brave")
    @Operation(summary = "Given the query, use Brave to do a web search")
    public BraveSearchResponse search(@RequestParam String query) {
        return braveMcpClient.search(query);
    }

    @GetMapping("/fetch")
    @Operation(summary = "Given the url and instructions, use fetch MCP to do a web fetch")
    public Object fetch(@RequestParam String url, @RequestParam String instructions) {
        return fetchMcpClient.fetch(url, instructions);
    }
}
