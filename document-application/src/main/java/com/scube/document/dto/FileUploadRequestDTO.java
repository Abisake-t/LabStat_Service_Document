package com.scube.document.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class FileUploadRequestDTO {
    @NotNull
    private MultipartFile file;
}
