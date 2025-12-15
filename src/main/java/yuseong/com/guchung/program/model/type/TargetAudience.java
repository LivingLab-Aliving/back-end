package yuseong.com.guchung.program.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TargetAudience {
    CHILD("어린이"),
    TEENAGER("청소년"),
    ADULT("성인"),
    DISABLED("장애인"),
    ALL("전체");

    private final String description;
}