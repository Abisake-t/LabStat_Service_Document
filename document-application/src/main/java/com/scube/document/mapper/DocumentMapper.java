package com.scube.document.mapper;

import com.scube.document.dto.DocumentDto;
import com.scube.document.model.Document;
import com.scube.document.model.DocumentHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class DocumentMapper {
    public abstract DocumentDto toDto(Document document);

    public abstract Document toEntity(DocumentDto documentDto);

    public abstract List<DocumentDto> toDto(List<Document> documents);

    public abstract List<Document> toEntity(List<DocumentDto> documentDtos);

    @Mapping(source = "document.id", target = "id")
    @Mapping(source = "document.uuid", target = "uuid")
    @Mapping(source = "document.documentUrl", target = "documentUrl")
    public abstract DocumentDto toDocumentDto(DocumentHistory documentHistory);

    public abstract List<DocumentDto> toDocumentDto(List<DocumentHistory> documentHistories);
}