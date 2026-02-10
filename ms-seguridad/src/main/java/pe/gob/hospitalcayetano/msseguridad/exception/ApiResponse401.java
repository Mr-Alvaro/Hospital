package pe.gob.hospitalcayetano.msseguridad.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ApiResponse401 {
  @JsonProperty("metadata")
  private ApiDataResponse401 metadata;

  public ApiResponse401 metadata(ApiDataResponse401 metadata) {
    this.metadata = metadata;
    return this;
  }

  public ApiDataResponse401 getMetadata() {
    return metadata;
  }

  public void setMetadata(ApiDataResponse401 metadata) {
    this.metadata = metadata;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiResponse401 apiResponse401 = (ApiResponse401) o;
    return Objects.equals(this.metadata, apiResponse401.metadata);
  }

  @Override
  public int hashCode() {
    return Objects.hash(metadata);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiResponse401 {\n");
    
    sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
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

