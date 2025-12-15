package yuseong.com.guchung.program.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ApplicationRequestDto {

    @Getter
    @NoArgsConstructor
    public static class Apply {
        private List<AnswerDto> answers;
    }

    @Data
    @NoArgsConstructor
    public static class AnswerDto {
        private Long formItemId;
        private String answer;
    }
}