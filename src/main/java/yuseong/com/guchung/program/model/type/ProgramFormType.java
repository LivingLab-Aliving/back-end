package yuseong.com.guchung.program.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProgramFormType {
    TEXT("단답형"),
    RADIO("객관식");

    private final String description;
}