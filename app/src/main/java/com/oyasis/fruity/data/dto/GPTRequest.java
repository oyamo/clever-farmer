package com.oyasis.fruity.data.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GPTRequest {

@SerializedName("model")
@Expose
private String model;
@SerializedName("prompt")
@Expose
private String prompt;
@SerializedName("max_tokens")
@Expose
private Integer maxTokens;
@SerializedName("temperature")
@Expose
private Integer temperature;
@SerializedName("top_p")
@Expose
private Integer topP;
@SerializedName("n")
@Expose
private Integer n;
@SerializedName("stream")
@Expose
private Boolean stream;
@SerializedName("logprobs")
@Expose
private Object logprobs;
@SerializedName("stop")
@Expose
private String stop;

public String getModel() {
return model;
}

public void setModel(String model) {
this.model = model;
}

public String getPrompt() {
return prompt;
}

public void setPrompt(String prompt) {
this.prompt = prompt;
}

public Integer getMaxTokens() {
return maxTokens;
}

public void setMaxTokens(Integer maxTokens) {
this.maxTokens = maxTokens;
}

public Integer getTemperature() {
return temperature;
}

public void setTemperature(Integer temperature) {
this.temperature = temperature;
}

public Integer getTopP() {
return topP;
}

public void setTopP(Integer topP) {
this.topP = topP;
}

public Integer getN() {
return n;
}

public void setN(Integer n) {
this.n = n;
}

public Boolean getStream() {
return stream;
}

public void setStream(Boolean stream) {
this.stream = stream;
}

public Object getLogprobs() {
return logprobs;
}

public void setLogprobs(Object logprobs) {
this.logprobs = logprobs;
}

public String getStop() {
return stop;
}

public void setStop(String stop) {
this.stop = stop;
}

}