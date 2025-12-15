package yuseong.com.guchung.program.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProgramType {

    YUSEONG("유성형 프로그램"),
    AUTONOMOUS("자치형 프로그램");

    private final String description;
}