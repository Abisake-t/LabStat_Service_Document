package com.scube.document.model;

import com.scube.audit.auditable.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;

import static com.scube.document.model.Document.TABLE_NAME;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@ToString
@Table(name = TABLE_NAME)
@Audited
public class Document extends AuditableEntity {
    public static final String TABLE_NAME = "document";
    public static final String TABLE_ID = "document_id";

    @Size(max = 255)
    @Column(name = "document_url")
    private String documentUrl;

    @Size(max = 255)
    @Column(name = "content_type")
    private String contentType;

    @Size(max = 255)
    private String name;

    private Long size;

    @Size(max = 255)
    private String versionId;

    private Integer versionNumber;

    @Size(max = 255)
    private String path;

    private Boolean storedWithOriginalName;

    @Size(max = 255)
    @Column(name="md5_hash")
    private String md5Hash;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<DocumentHistory> documentHistory = new ArrayList<>();

    public void addHistory() {
        if (this.documentHistory == null) this.documentHistory = new ArrayList<>();
        this.documentHistory.add(new DocumentHistory(this));
    }
}