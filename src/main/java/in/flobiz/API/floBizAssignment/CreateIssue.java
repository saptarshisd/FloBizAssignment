package in.flobiz.API.floBizAssignment;

import com.google.gson.Gson;

import in.flobizAPI.utils.ApiUtils;

public class CreateIssue extends ApiUtils{
	
	
	public String getRequestBody() {
		
		CreateIssuePOJO createIssuePOJO = new CreateIssuePOJO();
		
		createIssuePOJO.setDescription("This is a new issue");
		createIssuePOJO.setFromemail("flobiztest@gmail.com");
		createIssuePOJO.setTitle("New Issue");
		
		Gson gson = new Gson();
		return gson.toJson(createIssuePOJO);
	}

}
