//package Model;
//
//import lombok.*;
//
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@ToString
//@EqualsAndHashCode
//@Data
//
//public class Joke {
//    //Instance variables:
//    private int jokeId;
//    private int creatorId;
//    private String jokeText;
//    private String status; //Status can be: 'pending', 'approved', or 'rejected'.
//}

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
public class Joke {
    // Instance variables:
    private int jokeId;
    private int creatorId;
    private String jokeText;
    private String setup;
    private String punchline;
    private String category;
    private String status; // Status can be: 'pending', 'approved', or 'rejected'.
}
