package in.flobiz.API.floBizAssignment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateIssuePOJO {
	

	@SerializedName("description")
	@Expose
	private String description;
	@SerializedName("fromemail")
	@Expose
	private String fromemail;
	@SerializedName("title")
	@Expose
	private String title;

	public String getDescription() {
	return description;
	}

	public void setDescription(String description) {
	this.description = description;
	}

	public String getFromemail() {
	return fromemail;
	}

	public void setFromemail(String fromemail) {
	this.fromemail = fromemail;
	}

	public String getTitle() {
	return title;
	}

	public void setTitle(String title) {
	this.title = title;
	}

	}

	
	


