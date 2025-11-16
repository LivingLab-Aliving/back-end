package yuseong.com.guchung.program.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "application_answer")
public class ApplicationAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_item_id", nullable = false)
    private ProgramFormItem formItem;

    @Lob
    private String answer;

    @Builder
    public ApplicationAnswer(Application application, ProgramFormItem formItem, String answer) {
        this.application = application;
        this.formItem = formItem;
        this.answer = answer;
    }
}