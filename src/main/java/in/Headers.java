package in;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class  Headers{

	@SerializedName("tenant_code")
	@Expose
	private String tenantCode;
	@SerializedName("user_id")
	@Expose
	private String userId;
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@SerializedName("client_code")
	@Expose
	private String clientCode;
	@SerializedName("channel_code")
	@Expose
	private String channelCode;
	@SerializedName("end_channel_code")
	@Expose
	private String endChannelCode;
	@SerializedName("stan")
	@Expose
	private String stan;
	@SerializedName("client_ip")
	@Expose
	private String clientIp;
	@SerializedName("transmission_datetime")
	@Expose
	private String transmissionDatetime;
	@SerializedName("operation_mode")
	@Expose
	private String operationMode;
	@SerializedName("run_mode")
	@Expose
	private String runMode;
	@SerializedName("actor_type")
	@Expose
	private String actorType;
	@SerializedName("user_handle_type")
	@Expose
	private String userHandleType;
	@SerializedName("user_handle_value")
	@Expose
	private String userHandleValue;
	@SerializedName("location")
	@Expose
	private String location;
	@SerializedName("function_code")
	@Expose
	private String functionCode;
	@SerializedName("function_sub_code")
	@Expose
	private String functionSubCode;

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getEndChannelCode() {
		return endChannelCode;
	}

	public void setEndChannelCode(String endChannelCode) {
		this.endChannelCode = endChannelCode;
	}

	public String getStan() {
		return stan;
	}

	public void setStan(String stan) {
		this.stan = stan;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getTransmissionDatetime() {
		return transmissionDatetime;
	}

	public void setTransmissionDatetime(String transmissionDatetime) {
		this.transmissionDatetime = transmissionDatetime;
	}

	public String getOperationMode() {
		return operationMode;
	}

	public void setOperationMode(String operationMode) {
		this.operationMode = operationMode;
	}

	public String getRunMode() {
		return runMode;
	}

	public void setRunMode(String runMode) {
		this.runMode = runMode;
	}

	public String getActorType() {
		return actorType;
	}

	public void setActorType(String actorType) {
		this.actorType = actorType;
	}

	public String getUserHandleType() {
		return userHandleType;
	}

	public void setUserHandleType(String userHandleType) {
		this.userHandleType = userHandleType;
	}

	public String getUserHandleValue() {
		return userHandleValue;
	}

	public void setUserHandleValue(String userHandleValue) {
		this.userHandleValue = userHandleValue;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getFunctionCode() {
		return functionCode;
	}

	public void setFunctionCode(String functionCode) {
		this.functionCode = functionCode;
	}

	public String getFunctionSubCode() {
		return functionSubCode;
	}

	public void setFunctionSubCode(String functionSubCode) {
		this.functionSubCode = functionSubCode;
	}

}
