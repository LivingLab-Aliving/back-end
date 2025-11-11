package yuseong.com.guchung.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    private LocalDate birth;

    private String gender;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String address;

    @Column(name = "email_send", nullable = false)
    private boolean emailSend = false;

    @Column(name = "alarm_send", nullable = false)
    private boolean alarmSend = false;

    @Column(name = "sns_send", nullable = false)
    private boolean snsSend = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    private List<Application> applications = new ArrayList<>();
}