package yuseong.com.guchung.program.model;

import jakarta.persistence.*;
import lombok.*;
import yuseong.com.guchung.program.model.type.ProgramFormType;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramFormItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    private String label;

    @Enumerated(EnumType.STRING)
    private ProgramFormType type;

    private boolean required;

    @ElementCollection
    private List<String> options;
}