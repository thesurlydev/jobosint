package com.jobosint.model;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record ApplicationEventParticipant(UUID applicationEventId,
                                          UUID participantId) {
}
