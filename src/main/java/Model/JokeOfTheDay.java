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

public class JokeOfTheDay {
    //Instance variables:
    private int jodId;
    private int jokeId;
    private int totalVotes;
}
