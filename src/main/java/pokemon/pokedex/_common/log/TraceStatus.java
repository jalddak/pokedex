package pokemon.pokedex._common.log;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TraceStatus {

    private Long startTimeMs;
    private String message;

    public TraceStatus(Long startTimeMs, String message) {
        this.startTimeMs = startTimeMs;
        this.message = message;
    }
}
