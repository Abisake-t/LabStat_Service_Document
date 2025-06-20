package com.scube.document.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {
    private Long id;
    private UUID uuid;
    private String documentUrl;
    private String contentType;
    private String name;
    private Long size;
    private String versionId;
    private String versionNumber;
    private Boolean storedWithOriginalName;
    private String md5Hash;
}