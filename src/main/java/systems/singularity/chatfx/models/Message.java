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
 * Message
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-07-03T19:39:03.681Z")
public class Message {
  @SerializedName("id")
  private Integer id = null;

  @SerializedName("content")
  private String content = null;

  @SerializedName("status")
  private String status = null;

  @SerializedName("time")
  private String time = null;

  @SerializedName("authorId")
  private Integer authorId = null;

  @SerializedName("groupId")
  private Integer groupId = null;

  public Message id(Integer id) {
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

  public Message content(String content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   *
   * @return content
   **/
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Message status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   *
   * @return status
   **/
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Message time(String time) {
    this.time = time;
    return this;
  }

  /**
   * Get time
   *
   * @return time
   **/
  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public Message authorId(Integer authorId) {
    this.authorId = authorId;
    return this;
  }

  /**
   * Get authorId
   *
   * @return authorId
   **/
  public Integer getAuthorId() {
    return authorId;
  }

  public void setAuthorId(Integer authorId) {
    this.authorId = authorId;
  }

  public Message groupId(Integer groupId) {
    this.groupId = groupId;
    return this;
  }

  /**
   * Get groupId
   *
   * @return groupId
   **/
  public Integer getGroupId() {
    return groupId;
  }

  public void setGroupId(Integer groupId) {
    this.groupId = groupId;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Message message = (Message) o;
    return Objects.equals(this.id, message.id) &&
            Objects.equals(this.content, message.content) &&
            Objects.equals(this.status, message.status) &&
            Objects.equals(this.time, message.time) &&
            Objects.equals(this.authorId, message.authorId) &&
            Objects.equals(this.groupId, message.groupId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, content, status, time, authorId, groupId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Message {\n");

    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    authorId: ").append(toIndentedString(authorId)).append("\n");
    sb.append("    groupId: ").append(toIndentedString(groupId)).append("\n");
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

