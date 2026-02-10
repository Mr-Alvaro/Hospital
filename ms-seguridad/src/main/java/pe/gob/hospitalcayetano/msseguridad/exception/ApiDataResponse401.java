package pe.gob.hospitalcayetano.msseguridad.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ApiDataResponse401 {
  @JsonProperty("status")
  private Integer status;

  @JsonProperty("message")
  private String message;

  public ApiDataResponse401 status(Integer status) {
    this.status = status;
    return this;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public ApiDataResponse401 message(String message) {
    this.message = message;
    return this;
  }

  /**
   * descripción: mensaje de respuesta HTTP  <br> formato: libre
   * @return message
  */

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiDataResponse401 apiDataResponse401 = (ApiDataResponse401) o;
    return Objects.equals(this.status, apiDataResponse401.status) &&
        Objects.equals(this.message, apiDataResponse401.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiDataResponse401 {\n");
    
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

