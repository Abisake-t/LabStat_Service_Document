package com.scube.document.dto;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class FileUploadResponseDTO {
    private UUID documentUUID;
    private String documentUrl;
}
