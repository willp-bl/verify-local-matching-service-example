package uk.gov.ida.verifylocalmatchingserviceexample.contracts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchingAttributesValueDto<T> {
    @NotNull
    private T value;
    private DateTime from;
    private DateTime to;
    @NotNull
    private Boolean verified;

    @JsonCreator
    public MatchingAttributesValueDto(@JsonProperty("value") T value,
                                      @JsonProperty("from") DateTime from,
                                      @JsonProperty("to") DateTime to,
                                      @JsonProperty("verified") Boolean verified) {
        this.value = value;
        this.from = from;
        this.to = to;
        this.verified = verified;
    }

    public T getValue() {
        return value;
    }

    public DateTime getFrom() {
        return from;
    }

    public DateTime getTo() {
        return to;
    }

    public Boolean getVerified() {
        return verified;
    }
}
