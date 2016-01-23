package memorizer.freecoders.com.flashcards.json;

/**
 * Created by alex-mac on 27.11.15.
 */
public class UserDetails {

    public String name = "";
    public String phone = "";
    public String email = "";
    public String avatar = "";
    public String id = "";
    public String socket_id = "";
    public Integer status;
    public String locale = "";

    public void setNullFields(){
        if (name.isEmpty()) name = null;
        if (phone.isEmpty()) phone = null;
        if (email.isEmpty()) email = null;
        if (avatar.isEmpty()) avatar = null;
        if (id.isEmpty()) id = null;
    }

    public final static UserDetails cloneUser (UserDetails user) {
        UserDetails clone = new UserDetails();
        clone.name = user.name;
        clone.phone = user.phone;
        clone.email = user.email;
        clone.avatar = user.avatar;
        clone.id = user.id;
        clone.socket_id = user.socket_id;
        clone.status = user.status;
        clone.locale = user.locale;
        return clone;
    }
}
