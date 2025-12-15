package yuseong.com.guchung.program.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "program_file")
public class ProgramFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_file_id")
    private Long id;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "file_url", nullable = false, length = 512)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Builder
    public ProgramFile(String originalName, String fileUrl, Program program) {
        this.originalName = originalName;
        this.fileUrl = fileUrl;
        this.program = program;
    }
}