package com.scube.document.model;

import com.scube.audit.auditable.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import static com.scube.document.model.DocumentHistory.TABLE_NAME;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
@Table(name = TABLE_NAME)
@Audited
public class DocumentHistory extends AuditableEntity {
    public static final String TABLE_NAME = "document_history";
    public static final String TABLE_ID = "document_history_id";

    public DocumentHistory(Document document) {
        versionId = document.getVersionId();
        versionNumber = document.getVersionNumber();
        contentType = document.getContentType();
        name = document.getName();
        size = document.getSize();
        this.document = document;
    }

    @Size(max = 255)
    private String versionId;

    @Size(max = 255)
    @Column(name = "content_type")
    private String contentType;

    @Size(max = 255)
    private String name;

    private Long size;

    private Integer versionNumber;

    @ManyToOne
    @JoinColumn(name = Document.TABLE_ID)
    private Document document;
}
