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
    //Instance variables:
    private int voteId;
    private int userId;
    private int jokeId;
}
