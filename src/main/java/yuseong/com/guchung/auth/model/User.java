package yuseong.com.guchung.auth.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import yuseong.com.guchung.program.model.Application;
import yuseong.com.guchung.program.model.ProgramLike;

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
    private Integer userId;

    @Column(name = "oauth_id", unique = true)
    private String oauthId;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramLike> likedPrograms = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserFile> documents = new ArrayList<>();

    @Builder
    public User(String oauthId, String email, String name, LocalDate birth, String gender, String phoneNumber, String address) {
        this.oauthId = oauthId;
        this.email = email;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public void updateInfo(String name, String phoneNumber, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}