package com.scube.document.dto;

import java.util.UUID;

public record FileUpdateResponse(UUID documentUuid, Integer version) {}
