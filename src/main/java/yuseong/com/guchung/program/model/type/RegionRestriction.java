package yuseong.com.guchung.program.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RegionRestriction {
    NONE("제한 없음"),
    YUSEONG("유성구민"),
    DONGGU("동구민"),
    SEOGU("서구민"),
    JUNGGU("중구민"),
    DAEDEOK("대덕구민");

    private final String description;
}