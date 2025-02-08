package fun.jiucai.common.protocol.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author godotg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private long id;

    private String name;

    private long ctime;

    private long phoneNumber;

    private int ask;

    private int draw;

    private int login;

    private int cost;
}
