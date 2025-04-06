package com.jobosint.problem;

import java.util.List;

public record ExistingSolution(String name,
                               String description,
                               String url,
                               Long estimatedMarketSize,
                               Long estimatedMarketShare,
                               Long estimatedUserSatisfactionScoreOneToTen,
                               String type,
                               List<Source> sources) {
}
