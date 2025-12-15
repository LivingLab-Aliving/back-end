package yuseong.com.guchung.program.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yuseong.com.guchung.auth.model.User;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "program_like",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "program_id"})})
public class ProgramLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Builder
    public ProgramLike(User user, Program program) {
        this.user = user;
        this.program = program;
    }
}
