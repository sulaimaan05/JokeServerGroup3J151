package Model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Data
public class Vote {
    private int voteId;
    private int userId;
    private int jokeId;
    private int voteValue; // <-- add this
}