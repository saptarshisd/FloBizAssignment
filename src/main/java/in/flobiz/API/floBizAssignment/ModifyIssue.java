package in.flobiz.API.floBizAssignment;

import com.google.gson.Gson;

public class ModifyIssue {
	public String getRequestBody() {

		ModifyIssuePOJO modifyIssuePOJO = new ModifyIssuePOJO();

		
		modifyIssuePOJO.setTitle("New Issue");

		Gson gson = new Gson();
		return gson.toJson(modifyIssuePOJO);
	}

}
