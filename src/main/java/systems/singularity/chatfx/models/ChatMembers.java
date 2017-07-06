package systems.singularity.chatfx.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by phts on 06/07/17.
 */
public class ChatMembers {
    @SerializedName("chat")
    public Chat chat;

    @SerializedName("members")
    public Member[] members;

    public ChatMembers(Chat chat, Member[] members) {
        this.chat = chat;
        this.members = members;
    }
}
