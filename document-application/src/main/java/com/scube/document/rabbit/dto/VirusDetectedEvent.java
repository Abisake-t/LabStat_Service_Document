package com.scube.document.rabbit.dto;

import com.scube.rabbit.core.fanout.publisher.IRabbitFanoutPublisher;

public record VirusDetectedEvent(String filename) implements IRabbitFanoutPublisher {}

