package yuseong.com.guchung.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "instructor")
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instructor_id")
    private Integer instructorId;

    @Column(nullable = false)
    private String name;

    @Lob
    private String introduce;

    private String contact;

    @OneToMany(mappedBy = "instructor")
    private List<Program> programs = new ArrayList<>();
}