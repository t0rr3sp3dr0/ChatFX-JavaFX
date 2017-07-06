/*
 * ChatFX
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package systems.singularity.chatfx.models;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Member
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-07-03T19:39:03.681Z")
public class Member {
  @SerializedName("id")
  private Integer id = null;

  @SerializedName("chatId")
  private Integer chatId = null;

  @SerializedName("userUsername")
  private String userUsername = null;

  public Member id(Integer id) {
    this.id = Math.abs(id);
    return this;
  }

  /**
   * Get id
   *
   * @return id
   **/
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Member chatId(Integer chatId) {
    this.chatId = chatId;
    return this;
  }

  /**
   * Get chatId
   *
   * @return chatId
   **/
  public Integer getChatId() {
    return chatId;
  }

  public void setChatId(Integer chatId) {
    this.chatId = chatId;
  }

  public Member userUsername(String userUsername) {
    this.userUsername = userUsername;
    return this;
  }

  /**
   * Get userUsername
   *
   * @return userUsername
   **/
  public String getUserUsername() {
    return userUsername;
  }

  public void setUserUsername(String userUsername) {
    this.userUsername = userUsername;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Member member = (Member) o;
    return Objects.equals(this.id, member.id) &&
            Objects.equals(this.chatId, member.chatId) &&
            Objects.equals(this.userUsername, member.userUsername);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, chatId, userUsername);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Member {\n");

    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    chatId: ").append(toIndentedString(chatId)).append("\n");
    sb.append("    userUsername: ").append(toIndentedString(userUsername)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

