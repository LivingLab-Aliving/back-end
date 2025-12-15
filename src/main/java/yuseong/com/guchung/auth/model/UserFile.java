package yuseong.com.guchung.auth.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yuseong.com.guchung.auth.model.type.DocumentType; // DocumentType import

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_file")
public class UserFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_file_id")
    private Long id;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false, length = 512)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public UserFile(String originalName, String fileUrl, DocumentType documentType, User user) {
        this.originalName = originalName;
        this.fileUrl = fileUrl;
        this.documentType = documentType;
        this.user = user;
    }
}