package in.flobizAPI.utils;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.Reporter;

import redis.clients.jedis.Jedis;

public class DBUtils extends JavaUtils {
	private static final String String = null;
	protected Connection conn;
	protected Statement stmt;
	private String cspCode;
	public static String agentComm, commTDS, dispCharge, realCharge;
	public static String realChargeForUpgrade;

	public Connection createConnection(String dbSchemaName) {

		String dbNpActor = configProperties.get("dbUrl") + dbSchemaName + "?verifyServerCertificate=false&useSSL=true";
		String jdbcDriver = configProperties.get("jdbcDriver");
		try {
			Class.forName(jdbcDriver);
			conn = DriverManager.getConnection(dbNpActor, configProperties.get("dbUserName"),
					configProperties.get("dbPassword"));
			stmt = conn.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;

	}

	public Connection createConnection(String dburl, String dbSchemaName) {

		String dbNpActor = dburl + dbSchemaName;
		String jdbcDriver = configProperties.get("jdbcDriver");
		try {
			Class.forName(jdbcDriver);
			conn = DriverManager.getConnection(dbNpActor, configProperties.get("dbUserName"),
					configProperties.get("dbPassword"));
			stmt = conn.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;

	}

	public void closeConnection(Connection conn) {

		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("Unable to close the connection due to below error..!");
			e.printStackTrace();
		}
	}

	public String[] readMasterDataRecord(Connection conn, String actualCode, String actualValue, String partnerCode,
			String actualDataType) // partnerCode and actual
	{
		String dataSubType = null, dataType = null, value = null, version = null;
		try {

			String query1 = "select * from `platform_master_data` where `code`='" + actualCode + "' and `value`='"
					+ actualValue + "' and `partner_code`='" + partnerCode + "' and `data_type`='" + actualDataType
					+ "';";

			ResultSet rs = stmt.executeQuery(query1);
			System.out.println("Queried the DB using : " + query1);

			rs.next();
			if (!rs.getString("data_sub_type").equalsIgnoreCase("323")) {
				dataSubType = rs.getString("data_sub_type");
				dataType = rs.getString("data_type");
				// value = rs.getString("value");
				version = rs.getString("version");
			}

		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! readMasterDataRecord failed..!");
			sqe.printStackTrace();
		}
		return new String[] { dataSubType, dataType, version };
	}

	public int returnTotalValuesBasedOnDataTypeAndPartnerCode(String partnerId, String dataType) {

		int count = 0;
		try {
			String query1 = "select count(`value`) as count from `platform_master_data` where `partner_code`='"
					+ partnerId + "' and `data_type`='" + dataType + "';";
			System.out.println(query1);
			ResultSet rs = stmt.executeQuery(query1);

			while (rs.next()) {

				count = rs.getInt("count");
			}
		} catch (Exception e) {
			System.out.println("Error connecting DB!! readMasterDataRecord failed..!");
			e.printStackTrace();
		}
		return count;
	}

	public int returnTotalValuesBasedOnDataTypeAndPartnerCode(String partnerId, String dataType, String sinceTime) {

		int count = 0;
		try {
			String query1 = "select count(`value`) as count from `platform_master_data` where `last_updated_on` > '"
					+ sinceTime + "' and `partner_code`='" + partnerId + "' and `data_type`='" + dataType + "';";
			ResultSet rs = stmt.executeQuery(query1);

			while (rs.next()) {

				count = rs.getInt("count");
			}
		} catch (Exception e) {
			System.out.println("Error connecting DB!! readMasterDataRecord failed..!");
			e.printStackTrace();
		}
		return count;
	}

	public String[] updateCreatedOnLastUpdatedOn(String createdOnTime, String lastUpdatedOnTime, String code,
			String dataType) {

		String createdOnActual = null, lastUpdatedOnActual = null;
		try {

			String query1 = "select * from `platform_master_data` where `code` ='" + code + "' and `data_type`='"
					+ dataType + "';";
			ResultSet rs = stmt.executeQuery(query1);

			while (rs.next()) {

				createdOnActual = rs.getString("created_on");
				lastUpdatedOnActual = rs.getString("last_updated_on");
			}

			String uQuery1 = "update `platform_master_data` set `created_on` ='" + createdOnTime
					+ "',`last_updated_on`='" + lastUpdatedOnTime + "' where code ='" + code + "' and `data_type`='"
					+ dataType + "';";
			stmt.executeUpdate(uQuery1);

			return new String[] { createdOnActual, lastUpdatedOnActual };
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! readMasterDataRecord failed..!");
			sqe.printStackTrace();
		}
		Reporter.log("FAILURE..! Exception found while updating 'createdOn' and 'lastUpdatedOn' value from DB..!");
		return null;
	}

	public String[] captureCreatedOnLastUpdatedOn(String code, String dataType) {

		String createdOnActual = null, lastUpdatedOnActual = null;
		// try{
		//
		String query1 = "select * from `platform_master_data` where `code` ='" + code + "' and `data_type`='" + dataType
				+ "';";

		try {
			ResultSet rs = stmt.executeQuery(query1);
			while (rs.next()) {
				createdOnActual = rs.getString("created_on");
				lastUpdatedOnActual = rs.getString("last_updated_on");
			}
		} catch (SQLException e) {
			System.out.println("Error connecting DB!! captureCreatedOnLastUpdatedOn failed..!");
			e.printStackTrace();
		}

		return new String[] { createdOnActual, lastUpdatedOnActual };
		// }
		// catch(SQLException sqe){
		//
		// sqe.printStackTrace();
		// return null;
		// }
		// Reporter.log("FAILURE..! Exception found while updating 'createdOn'
		// and 'lastUpdatedOn' value from DB..!");
		// return null;
	}

	public void setDelFlagTo(String[] codeAndDataType, String statusToSet) {
		try {
			String uQuery1 = "update `platform_master_data` set `del_flag`='" + statusToSet + "' where code ='"
					+ codeAndDataType[0] + "' and `data_type`='" + codeAndDataType[1] + "';";
			stmt.executeUpdate(uQuery1);

		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! readMasterDataRecord failed..!");
			sqe.printStackTrace();
		}
		Reporter.log("FAILURE..! Exception found while updating 'del_flag' value from DB..!");

	}

	public String captureDelFlagTo(String[] codeAndDataType) {

		String delFlagValue = null;
		try {
			String query1 = "select `del_flag` from `platform_master_data` where `code` ='" + codeAndDataType[0]
					+ "' and `data_type`='" + codeAndDataType[1] + "';";
			ResultSet rs = stmt.executeQuery(query1);

			while (rs.next()) {

				delFlagValue = rs.getString("del_flag");
			}

			return delFlagValue;
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! readMasterDataRecord failed..!");
			sqe.printStackTrace();
		}
		Reporter.log("FAILURE..! Exception found while updating 'del_flag' value from DB..!");
		return delFlagValue;
	}

	public String returnUuidForMaster(String mobileNumber) {

		String uuid = null;
		try {
			String query1 = "select `attr_value` from `user_attribute` where `user_id` IN (select `id` from `user` where `organization` IN ( select `id` from `organization` where `code` IN ( select `org_code` from `organization_devices` where `msisdn` = '"
					+ mobileNumber + "'))) and `attr_key`='UUID';";
			ResultSet rs = stmt.executeQuery(query1);

			while (rs.next()) {

				uuid = rs.getString("attr_value");
			}

			return uuid;
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! returnUuidForMaster failed..!");
			sqe.printStackTrace();
		}
		Reporter.log("FAILURE..! Exception found while returning UUID from Master DB..!");
		return uuid;
	}

	public void setUserStatus(String mobileNumber, String statusActiveOrInactive) {

		try {
			String uQuery1 = "update `user` set status='" + statusActiveOrInactive
					+ "' where `organization` IN ( select `id` from `organization` where `code` IN ( select `org_code` from `organization_devices` where `msisdn` = '"
					+ mobileNumber + "'));";
			stmt.executeUpdate(uQuery1);
			Reporter.log("User has been set to " + statusActiveOrInactive + "state");
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! setUserStatus failed..!");
			sqe.printStackTrace();
		}
		Reporter.log("FAILURE..! Exception found while updating 'user.status' from DB..!");
	}

	public String returnUsername(String mobileNumber) {

		String firstName = null;
		try {
			String query1 = "select * from `user` where `organization` IN ( select `id` from `organization` where `code` IN ( select `org_code` from `organization_devices` where `msisdn` = '"
					+ mobileNumber + "'));";
			ResultSet rs = stmt.executeQuery(query1);

			while (rs.next()) {

				firstName = rs.getString("first_name");
			}

			return firstName;
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! returnUsername failed..!");
			sqe.printStackTrace();
		}
		Reporter.log("FAILURE..! Exception found while returning Username from master DB..!");
		return null;

	}

	public String[] returnOrgCodeandName(String mobileNumber) {

		String orgCode = null, shopName = null;
		try {
			String query1 = "select * from `organization` where `code` IN ( select `org_code` from `organization_devices` where `msisdn` = '"
					+ mobileNumber + "')";
			ResultSet rs = stmt.executeQuery(query1);

			while (rs.next()) {

				orgCode = rs.getString("code");
				shopName = rs.getString("name");
			}

			return new String[] { orgCode, shopName };
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! returnOrgCodeandName failed..!");
			sqe.printStackTrace();
		}
		Reporter.log("FAILURE..! Exception found while returning OrgCodeandName from master DB..!");
		return null;

	}

	public ArrayList<String> returnCustomerDetailsC2A(String mobileNumber) {

		try {
			String query1 = "select ch.`customer_id`, " + "ch.`status`, " + "ch.`created_by`, "
					+ "cu.`customer_status`, " + "ca.`partner`, " + "ca.`status` " + "from `customer_handle` As ch "
					+ "join `customer` As cu on cu.`id`= ch.`customer_id`"
					+ "join `customer_attribute` As ca on ca.`customer_id`= cu.`id`" + "where ch.`handle_value` = '"
					+ mobileNumber + "';";
			System.out.println(query1);
			ResultSet rs = stmt.executeQuery(query1);
			ResultSetMetaData metadata = rs.getMetaData();
			int numberOfColumns = metadata.getColumnCount();
			ArrayList<String> arrayList = new ArrayList<String>();
			while (rs.next()) {
				int i = 1;
				while (i <= numberOfColumns) {
					arrayList.add(rs.getString(i++));
				}
			}
			return arrayList;
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! resetSessionTokenFromMaster failed..!");
			sqe.printStackTrace();
		}
		return null;
	}
	public ArrayList<String> returnCustomerDetailsW2A(String mobileNumber) {

		try {
			String query1 = "select ch.`customer_id`, " + "ch.`status`, " + "ch.`created_by`, "
					+ "cu.`customer_status`, " + "ca.`partner`, " + "ca.`attr_key`, " + "ca.`attr_value`, "
					+ "ca.`status` " + "from `customer_handle` As ch "
					+ "join `customer` As cu on cu.`id`= ch.`customer_id`"
					+ "join `customer_attribute` As ca on ca.`customer_id`= cu.`id`" + "where ch.`handle_value` = '"
					+ mobileNumber + "';";
			ResultSet rs = stmt.executeQuery(query1);
			ResultSetMetaData metadata = rs.getMetaData();
			int numberOfColumns = metadata.getColumnCount();
			ArrayList<String> arrayList = new ArrayList<String>();
			while (rs.next()) {
				int i = 1;
				while (i <= numberOfColumns) {
					arrayList.add(rs.getString(i++));
				}
			}
			return arrayList;
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! resetSessionTokenFromMaster failed..!");
			sqe.printStackTrace();
		}
		return null;
	}

	public String[] returnRemitterBasicDetails(String mobileNumber) {

		String novopayStatus = null, firstName = null, gender = null, partnerStatus = null;
		try {
			String query1 = "select ch.`status` As nStatus," + "cu.`first_name`,cu.`gender`, "
					+ "ca.`status` As pStatus " + "from `customer_handle` As ch "
					+ "join `customer` As cu on cu.`id`= ch.`customer_id`" + "join `customer_attribute` As ca "
					+ "on ca.`customer_id`= cu.`id`" + "where ch.`handle_value` = '" + mobileNumber + "';";
			ResultSet rs = stmt.executeQuery(query1);

			while (rs.next()) {

				novopayStatus = rs.getString("nStatus");
				firstName = rs.getString("first_name");
				gender = rs.getString("gender");
				partnerStatus = rs.getString("pStatus");
			}
			return new String[] { novopayStatus, firstName, gender, partnerStatus };
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! returnRemitterBasicDetails failed..!");
			sqe.printStackTrace();
		}
		Reporter.log("FAILURE..! Exception found while returnRemitterBasicDetails from np_actor DB..!");
		return null;
	}

	//
	public String[] returnAccountDetailsFromDB(String mobileNumber) {

		String accountType = null, accountValue = null;
		try {
			String query1 = "SELECT `type`,`pan` FROM `fin_inst_master` "
					+ "WHERE pan IN (SELECT fin_instrument_number FROM `customer_fin_instrument_mapping` "
					+ "WHERE `type` = 'BANK_ACCOUNT' AND customer_id IN (SELECT customer_id FROM customer_handle WHERE `status` ='ACTIVE' AND handle_value = '"
					+ mobileNumber + "')) ;";
			ResultSet rs = stmt.executeQuery(query1);

			while (rs.next()) {

				accountType = rs.getString("type");
				accountValue = rs.getString("pan");
			}
			return new String[] { accountType, accountValue };
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! returnAccountDetails failed..!");
			sqe.printStackTrace();
		}
		Reporter.log("FAILURE..! Exception found while returnAccountDetails from np_actor DB..!");
		return null;
	}

	public String returnBeneficiaryAccValidation(String remitterMobileNo, String beneficiaryMobNo) {

		try {
			String query = "SELECT `customer_id` AS `remitter_id` FROM `customer_handle` WHERE `handle_value` = '"
					+ remitterMobileNo + "'";
			ResultSet rs = stmt.executeQuery(query);
			rs.last();
			String remitter_id = rs.getString("remitter_id");
			query = "SELECT `customer_id` AS `beneficiary_id` FROM `customer_handle` WHERE `handle_value` = '"
					+ beneficiaryMobNo + "'";
			rs = stmt.executeQuery(query);
			rs.last();
			String beneficiary_id = rs.getString("beneficiary_id");

			query = "SELECT * FROM `customer_beneficiary_mapping` WHERE `remitter_customer_id` = '" + remitter_id
					+ "' AND `beneficiary_customer_id` = '" + beneficiary_id + "'";
			rs = stmt.executeQuery(query);
			rs.last();

			return rs.getString("validated_on");

		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;
	}

	public String getAgentWalletRefNo(String mobileNumber) {
		try {
			Connection conn1 = createConnection(configProperties.get("master"));
			Statement stmt = conn1.createStatement();
			String query = "SELECT  oa1.attr_value external_ref_no FROM organization o, organization_attribute oa1, "
					+ " organization_attribute oa2, `user` u, user_attribute ua1, user_attribute ua2, org_stlmnt_info osi "
					+ "WHERE o.id = osi.organization_id AND o.id = oa1.orgnization_id AND o.status = 'ACTIVE'  "
					+ "AND o.id = oa2.orgnization_id AND o.id = u.organization AND oa1.attr_key = 'WALLET_ACCOUNT_NUMBER' "
					+ "AND oa2.attr_key = 'OTC_MERCHANT_WALLET_ACCOUNT_NUMBER' AND ua2.attr_key = 'MSISDN' AND u.id = ua2.user_id "
					+ "AND u.id = ua1.user_id AND ua1.attr_key = 'MSISDN' AND ua1.attr_value = '" + mobileNumber
					+ "' ORDER BY osi.start_date DESC LIMIT 1 ";

			ResultSet rs = stmt.executeQuery(query);
			rs.last();
			return rs.getString("external_ref_no");
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! getWalletRefNo failed..!");
			sqe.printStackTrace();
		}
		return null;
	}

	public String getWalletRefNo(String mobileNumber) {

		try {
			Connection conn1 = createConnection(configProperties.get("npActor"));
			Statement stmt = conn1.createStatement();
			String query = null;

			query = "SELECT `external_ref_no` FROM `wallet` WHERE `inst_id` IN (SELECT `id` FROM `fin_inst_master` WHERE `pan` IN (SELECT `fin_instrument_number` FROM `customer_fin_instrument_mapping` WHERE `customer_id` IN (SELECT `customer_id` FROM `customer_handle` WHERE `handle_value` = '"
					+ mobileNumber + "')))";

			ResultSet rs = stmt.executeQuery(query);
			rs.last();
			return rs.getString("external_ref_no");

		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! getWalletRefNo failed..!");
			sqe.printStackTrace();
		}
		return null;

	}

	public HashMap<String, String> getKYCDetails(String mobileNumber, String handleType) {
		try {
			Connection conn1 = createConnection(configProperties.get("npActor"));
			Statement stmt = conn1.createStatement();
			HashMap<String, String> details = new HashMap<String, String>();
			String query = "SELECT customer_id from customer_handle where handle_type='" + handleType
					+ "' AND handle_value='" + mobileNumber + "'  AND `STATUS` = 'ACTIVE'";

			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			String customer_id = rs.getString("customer_id");

			query = "SELECT handle_value from customer_handle where handle_type='EMAIL' AND customer_id='" + customer_id
					+ "' AND `STATUS` = 'ACTIVE'";

			rs = stmt.executeQuery(query);
			if (rs.next()) {
				details.put("email", rs.getString("handle_value"));
			}
			// else {
			// details.put("email", null);
			// }

			query = "SELECT handle_value from customer_handle where handle_type='MSISDN' AND customer_id='"
					+ customer_id + "' AND `STATUS` = 'ACTIVE'";

			rs = stmt.executeQuery(query);
			if (rs.next()) {

				details.put("mobileNumber", rs.getString("handle_value"));
			}
			// else {
			// details.put("mobileNumber", null);
			// }

			String queryCustDetails = "SELECT * FROM customer where `id` = '" + customer_id + "'";
			rs = stmt.executeQuery(queryCustDetails);
			if (rs.next()) {

				details.put("firstName", rs.getString("first_name"));

				details.put("cifId", rs.getString("cif_id"));
				details.put("gender", rs.getString("gender"));

				details.put("dob", rs.getString("dob"));
			}
			// else {
			// details.put("firstName", null);
			//
			// details.put("cifId", null);
			// details.put("gender", null);
			//
			// details.put("dob", null);
			// }

			String queryPOADocumentsId = "SELECT * FROM document WHERE id = ( Select document_id FROM customer_documents_mapping where customer_id ='"
					+ customer_id + "' AND purpose = 'ADDRESS_PROOF')";

			rs = stmt.executeQuery(queryPOADocumentsId);
			if (rs.next()) {
				details.put("poaDocumentStoreUrn", rs.getString("document_store_urn"));
				details.put("poaNumberOnDocument", rs.getString("number_on_document"));
				details.put("poaType", rs.getString("type"));
			}
			String queryPOIDocuments = "SELECT * FROM document WHERE id = ( Select document_id FROM customer_documents_mapping where customer_id ='"
					+ customer_id + "' AND purpose = 'ID_PROOF')";

			rs = stmt.executeQuery(queryPOIDocuments);
			if (rs.next()) {
				details.put("poiDocumentStoreUrn", rs.getString("document_store_urn"));
				details.put("poiNumberOnDocument", rs.getString("number_on_document"));
				details.put("poiType", rs.getString("type"));
			}
			return details;

		} catch (SQLException sqe) {
			sqe.printStackTrace();
		} catch (NullPointerException ne) {
			System.out.println("Something is null in db");
			ne.printStackTrace();
		}
		return null;
	}

	public HashMap<String, String> getRegisterCustomerdetails(String mobileNumber, String handleType) {

		try {
			Connection conn1 = createConnection(configProperties.get("npActor"));
			Statement stmt = conn1.createStatement();
			HashMap<String, String> details = new HashMap<String, String>();
			String query = "SELECT customer_id from customer_handle where handle_type='" + handleType
					+ "' AND handle_value='" + mobileNumber + "'";
			// System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			rs.last();
			String customer_id = rs.getString("customer_id");
			details.put("actorId", customer_id);

			query = "SELECT * FROM customer where `id` = '" + customer_id + "'";
			// System.out.println(query);
			rs = stmt.executeQuery(query);
			rs.last();
			String cif_id = rs.getString("cif_id");
			// System.out.println("cif_id :: " + cif_id);

			conn1 = createConnection(configProperties.get("transactionLog"));
			stmt = conn1.createStatement();

			query = "SELECT * FROM tx_detail where cr_party_id = '" + cif_id + "'";
			// System.out.println(query);
			rs = stmt.executeQuery(query);
			rs.last();

			// String[] dateTime = rs.getString("tx_leg_end_time").split(" ");
			//
			// DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			// details.put("Date", df.format(df.parse(dateTime[0])));
			// System.out.println(details.get("Date"));
			// df = new SimpleDateFormat("HH:mm:ss");
			// details.put("Time", df.format(df.parse(dateTime[1])));
			// System.out.println(details.get("Time"));

			details.put("CSP", rs.getString("dr_party_name"));
			// System.out.println("CSP " + rs.getString("dr_party_name"));
			details.put("Txn_Id", rs.getString("tx_ref_code"));
			details.put("CustName", rs.getString("cr_party_name"));

			details.put("Status", rs.getString("status"));

			DecimalFormat df = new DecimalFormat("#.00");

			details.put("Amount", "Rs. " + df.format(rs.getFloat("posted_amount") / 100));
			details.put("Charges", "Rs. " + df.format(rs.getFloat("charges") / 100));

			return details;

		} catch (SQLException sqe) {
		}
		// catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return null;

	}

	public HashMap<String, String> getCustomerdetails(String mobileNumber, String handleType) {

		try {
			Connection conn1 = createConnection(configProperties.get("npActor"));
			Statement stmt = conn1.createStatement();
			HashMap<String, String> details = new HashMap<String, String>();
			String query = "SELECT customer_id from customer_handle where handle_type='" + handleType
					+ "' AND handle_value='" + mobileNumber + "'";
			// System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			rs.last();
			String customer_id = rs.getString("customer_id");
			details.put("actorId", customer_id);

			query = "SELECT * FROM customer where `id` = '" + customer_id + "'";
			// System.out.println(query);
			rs = stmt.executeQuery(query);
			rs.last();
			String cif_id = rs.getString("cif_id");
			// System.out.println("cif_id :: " + cif_id);

			// conn1 = createConnection(configProperties.get("transactionLog"));
			// stmt = conn1.createStatement();

			query = "SELECT * FROM tx_audit WHERE debit_party_id_value = '" + cif_id + "' ORDER BY id DESC LIMIT 1";
			// System.out.println(query);
			rs = stmt.executeQuery(query);
			rs.last();

			// String[] dateTime = rs.getString("tx_leg_end_time").split(" ");
			//
			// DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			// details.put("Date", df.format(df.parse(dateTime[0])));
			// System.out.println(details.get("Date"));
			// df = new SimpleDateFormat("HH:mm:ss");
			// details.put("Time", df.format(df.parse(dateTime[1])));
			// System.out.println(details.get("Time"));

			details.put("CSP", rs.getString("credit_party_name"));
			details.put("Txn_Id", rs.getString("novopay_ref_code"));
			// System.out.println(
			// "CSP " + rs.getString("debit_party_name") + " txn _id :: " +
			// rs.getString("novopay_ref_code"));

			details.put("CustName", rs.getString("debit_party_name"));

			details.put("Status", rs.getString("status"));

			DecimalFormat df = new DecimalFormat("#.00");

			details.put("Fee", "Rs. " + df.format(rs.getFloat("tx_charge") / 100));

			return details;

		} catch (SQLException sqe) {
		}
		// catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		return null;

	}

	public String getTransactionDetailCharge(String componentCode, String accountingType, String componentType,
			Float amount, String agentMobNumber) {

		try {
			Connection conn1 = createConnection(configProperties.get("limitCharges"));
			Statement stmt = conn1.createStatement();
			ResultSet rs;
			String query;
			float baseCharge, percentage;
			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.CEILING);
			if (componentCode.equals("SOURCE_AMOUNT")) {
				closeConnection(conn1);
				return df.format(amount);
			} else if (componentCode.equals("CHRG_RBL_COMM_TDS")) {
				conn1 = createConnection(configProperties.get("master"));
				stmt = conn1.createStatement();
				String queryPANNumber = "SELECT `attr_value` FROM `organization_attribute` WHERE `attr_key` = 'PAN' AND `orgnization_id` = (SELECT `organization` FROM `user` WHERE `id` = "
						+ "(SELECT `user_id` FROM `user_attribute` WHERE `attr_value` = '" + agentMobNumber + "'))";
				rs = stmt.executeQuery(queryPANNumber);
				rs.last();
				conn1 = createConnection(configProperties.get("config"));
				stmt = conn1.createStatement();
				if ((rs.getString("attr_value") == null) || (rs.getString("attr_value").isEmpty())) {
					query = "SELECT prop_value FROM configuration WHERE prop_key ='novopay.agent.tds.percentage.without.pan'";
					// System.out.println(query);
					rs = stmt.executeQuery(query);

				} else {
					query = "SELECT prop_value FROM configuration WHERE prop_key ='novopay.agent.tds.percentage.with.pan'";
					// System.out.println(query);
					rs = stmt.executeQuery(query);

				}
				rs.last();
				float tdsPercentage = rs.getFloat("prop_value") / 100;
				df = new DecimalFormat("0.00");
				df.setRoundingMode(RoundingMode.CEILING);
				if ((this.agentComm == null) || (this.agentComm.isEmpty())) {

					conn1 = createConnection(configProperties.get("limitCharges"));
					stmt = conn1.createStatement();
					// CUSTOMER_FIRST_CASHIN_ASSISTED_AGENT_COMM [5]
					query = "SELECT * FROM charge_category_slabs WHERE category_code = 'CUSTOMER_FIRST_CASHIN_ASSISTED_AGENT_COMM' AND accounting_type = 'DEBIT' AND charge_type ='COMMISSION' AND '"
							+ (amount * 100) + "' BETWEEN slab_from_amount AND slab_to_amount";
					rs = stmt.executeQuery(query);
					rs.last();
					baseCharge = rs.getFloat("base_charge") / 100;
					percentage = rs.getFloat("percentage") / 100;

					agentComm = df.format(baseCharge + (amount * (percentage / 100)));

					commTDS = df.format((baseCharge + (amount * (percentage / 100))) * (tdsPercentage / 100));
					// System.out.println("agentComm : " + agentComm +
					// "\ncommTDS : " + commTDS + "\nbaseCharge : "
					// + baseCharge + "\npercentage : " + percentage);
					closeConnection(conn1);
					return commTDS;
				} else {
					closeConnection(conn1);
					return df.format(Float.parseFloat(agentComm) * (tdsPercentage / 100));
				}

			} else if (componentCode.equals("CUSTOMER_FIRST_CASHIN_ASSISTED_AGENT_COMM")) {
				if (agentComm != null && !agentComm.isEmpty()) {
					closeConnection(conn1);
					return agentComm;
				} else {
					conn1 = createConnection(configProperties.get("limitCharges"));
					stmt = conn1.createStatement();
					// CUSTOMER_FIRST_CASHIN_ASSISTED_AGENT_COMM [5]
					query = "SELECT * FROM charge_category_slabs WHERE category_code = 'CUSTOMER_FIRST_CASHIN_ASSISTED_AGENT_COMM' AND accounting_type = 'DEBIT' AND charge_type ='COMMISSION' AND '"
							+ (amount * 100) + "' BETWEEN slab_from_amount AND slab_to_amount";
					// System.out.println(query);
					rs = stmt.executeQuery(query);
					rs.last();
					baseCharge = rs.getFloat("base_charge") / 100;
					percentage = rs.getFloat("percentage") / 100;
					df = new DecimalFormat("0.00");
					df.setRoundingMode(RoundingMode.CEILING);
					if (componentCode.equals("CUSTOMER_FIRST_CASHIN_ASSISTED_AGENT_COMM")) {
						agentComm = df.format(baseCharge + (amount * (percentage / 100)));
					}
					closeConnection(conn1);
					return df.format(baseCharge + (amount * (percentage / 100)));

				}

			} else if (componentCode.equals("CASH_AMOUNT")) {
				closeConnection(conn1);
				//
				if (amount > 0)
					df = new DecimalFormat("#.##");
				else
					df = new DecimalFormat("0.00");
				return df.format(amount);
				// if (this.dispCharge != null && !this.dispCharge.isEmpty()) {
				// System.out.println("disp charge :: "+df.format(amount +
				// Float.parseFloat(this.dispCharge)));
				// return df.format(amount + Float.parseFloat(this.dispCharge));
				//
				// } else {
				// query = "SELECT * FROM charge_category_slabs WHERE
				// category_code =
				// 'CUSTOMER_FIRST_CASHIN_ASSISTED_CHARGE_DISPLAY' AND
				// accounting_type = 'DISPLAY_TO_CUSTOMER' AND charge_type
				// ='CHARGE' AND '"
				// + (amount * 100) + "' BETWEEN slab_from_amount AND
				// slab_to_amount";
				// System.out.println(query);
				// rs = stmt.executeQuery(query);
				// rs.last();
				// baseCharge = rs.getFloat("base_charge") / 100;
				// System.out.println(baseCharge);
				// percentage = rs.getFloat("percentage") / 100;
				// System.out.println(percentage);
				// df = new DecimalFormat("#.00");
				// df.setRoundingMode(RoundingMode.CEILING);
				// this.dispCharge = df.format(baseCharge + (amount *
				// (percentage / 100)));
				// System.out.println("display charge :: " + this.dispCharge);
				// return df.format(amount + (baseCharge + (amount * (percentage
				// / 100))));
				// }

			}

			else {
				if ((componentCode.equals("CUSTOMER_FIRST_CASHIN_ASSISTED_CHARGE_DISPLAY"))
						&& (dispCharge != null && !dispCharge.isEmpty())) {
					closeConnection(conn1);
					return dispCharge;
				} else {
					conn1 = createConnection(configProperties.get("limitCharges"));
					stmt = conn1.createStatement();
					query = "SELECT * FROM charge_category_slabs WHERE category_code = '" + componentCode
							+ "' AND accounting_type = '" + accountingType + "' AND charge_type ='" + componentType
							+ "' AND '" + (amount * 100) + "' BETWEEN slab_from_amount AND slab_to_amount";
					rs = stmt.executeQuery(query);
					rs.last();
					baseCharge = rs.getFloat("base_charge") / 100;
					percentage = rs.getFloat("percentage") / 100;
					df = new DecimalFormat("0.00");
					df.setRoundingMode(RoundingMode.CEILING);
					if (componentCode.equals("CUSTOMER_FIRST_CASHIN_ASSISTED_CHARGE_DISPLAY")) {
						dispCharge = df.format(baseCharge + (amount * (percentage / 100)));
					}
					if (componentCode.equals("CUSTOMER_FIRST_CASHIN_ASSISTED_CUSTOMER_CHARGE")) {
						realCharge = df.format(baseCharge + (amount * (percentage / 100)));
					}
					if (componentCode.equals("CUSTOMER_KYC_UPGRADE_CUSTOMER_CHARGE")) {
						realChargeForUpgrade = df.format(baseCharge + (amount * (percentage / 100)));
					}
					closeConnection(conn1);

					return df.format(baseCharge + (amount * (percentage / 100)));
				}

			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return null;
	}

	public String getOTPMessage(String remitterMobileNumber) {

		try {

			conn = createConnection(configProperties.get("smsLog"));
			stmt = conn.createStatement();
			String query = "SELECT message FROM sms_log WHERE " + "sent_to = '" + remitterMobileNumber
					+ "' AND message LIKE '%OTP%' ORDER BY delivered_date DESC LIMIT 1";

			// System.out.println(query);
			ResultSet rs = stmt.executeQuery(query);
			rs.last();
			// System.out.println(rs.getString("message"));
			return rs.getString("message");
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! get OTP message failed..!");
			sqe.printStackTrace();
		}

		return null;
	}

	public String[] returnBeneficiaryDetails(String remitterMobileNumber, String beneficiaryMobileNumber) {

		String remittanceType = null, status = null;
		try {
			String query1 = "select * from `customer_beneficiary_mapping_attributes` where `customer_beneficiary_mapping_id` in(select `id` from `customer_beneficiary_mapping` where `remitter_customer_id` in(select `customer_id` from `customer_handle` where `handle_value`='"
					+ remitterMobileNumber
					+ "') and `beneficiary_customer_id` in(select `customer_id` from `customer_handle` where `handle_value`='"
					+ beneficiaryMobileNumber + "'));";
			ResultSet rs = stmt.executeQuery(query1);

			while (rs.next()) {

				remittanceType = rs.getString("remittance_type");
				status = rs.getString("status");
			}
			return new String[] { remittanceType, status };
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! returnBeneficiaryDetails failed..!");
			sqe.printStackTrace();
		}
		Reporter.log("FAILURE..! Exception found while returnBeneficiaryDetails from np_actor DB..!");
		return null;
	}

	public int getStatusInquiryCount(String agentMobileNo, String remitterMobileNo) {

		try {
			String query = "SELECT o.code "
					+ "FROM organization o, organization_attribute oa1,  organization_attribute oa2, `user` u, user_attribute ua1, user_attribute ua2, org_stlmnt_info osi WHERE o.id = osi.organization_id AND o.id = oa1.orgnization_id AND o.status = 'ACTIVE'  "
					+ "AND o.id = oa2.orgnization_id AND o.id = u.organization AND oa1.attr_key = 'WALLET_ACCOUNT_NUMBER'   "
					+ "AND oa2.attr_key = 'OTC_MERCHANT_WALLET_ACCOUNT_NUMBER' AND ua2.attr_key = 'MSISDN' "
					+ "AND u.id = ua2.user_id AND u.id = ua1.user_id "
					+ "AND ua1.attr_key = 'MSISDN' AND ua1.attr_value = '" + agentMobileNo + "' "
					+ "ORDER BY osi.start_date DESC LIMIT 1 ;";

			conn = createConnection(configProperties.get("master"));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.last();
			cspCode = rs.getString("code");

			conn = createConnection(configProperties.get("npRemittance"));
			query = "SELECT count(*)  FROM `remittance_outward_table` WHERE remitter_msisdn = '" + remitterMobileNo
					+ "' AND csp_code = '" + cspCode + "' AND `created_on` > CURRENT_DATE - INTERVAL '60' DAY ;";
			System.out.println(query);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			rs.last();
			return rs.getInt(1);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	public HashMap<String, String> getStatusInquiryDetails(String remitterMobileNo, String novopayReferenceNo) {
		HashMap<String, String> details = new LinkedHashMap<String, String>();
		try {
			conn = createConnection(configProperties.get("npRemittance"));
			String query = "SELECT * FROM `remittance_outward_table` WHERE remitter_msisdn = '" + remitterMobileNo
					+ "' AND csp_code = '" + cspCode + "' AND `payment_ref_code` = '" + novopayReferenceNo + "'";
			System.out.println(query);

			stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery(query);
			DecimalFormat df = new DecimalFormat("#.00");
			rs.last();

			details.put("transactionType", rs.getString("remittance_type"));
			details.put("accountNumber", rs.getString("beneficiary_acc_num"));
			details.put("beneficiaryName", rs.getString("beneficiary_name"));
			details.put("transactionAmount", df.format(rs.getFloat("amount") / 100));
			details.put("transactionCharge", df.format(rs.getFloat("charge") / 100));
			details.put("partnerReferenceNo", rs.getString("bank_ref_code"));
			details.put("bankName", rs.getString("beneficiary_bank"));
			details.put("transactionCurrency", rs.getString("currency"));
			details.put("transactionStatus", rs.getString("status"));
			details.put("novopayReferenceNo", rs.getString("payment_ref_code"));
			// details.put("transactionNarration",
			// rs.getString("rejection_remarks"));
			details.put("transactionDate", rs.getString("created_on"));
			return details;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public HashMap<String, String> getTransactionRefundPrintDetails(String remitterMobileNo,
			String novopayReferenceNo) {

		HashMap<String, String> details = new LinkedHashMap<String, String>();
		try {
			conn = createConnection(configProperties.get("npRemittance"));
			String query = "SELECT * FROM `remittance_outward_table` WHERE remitter_msisdn = '" + remitterMobileNo
					+ "' AND `payment_ref_code` = '" + novopayReferenceNo + "'";
			System.out.println(query);

			stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery(query);
			DecimalFormat df = new DecimalFormat("#.00");
			rs.last();

			String dateTimeInStr = rs.getString("refund_completed_on");
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				java.util.Date date = simpleDateFormat.parse(dateTimeInStr);

				details.put("Date", new SimpleDateFormat("dd-MMM-yyyy").format(date));
				details.put("Time", new SimpleDateFormat("hh:mm:ss a").format(date));
			} catch (ParseException ex) {
				ex.printStackTrace();
			}
			details.put("CSP", rs.getString("csp_name"));
			details.put("CSPAgent", rs.getString("csp_user_name") + " ");
			details.put("Status", rs.getString("refund_status"));
			details.put("OriginalTxnId", rs.getString("bank_ref_code"));
			details.put("CustName", rs.getString("sender_name"));
			details.put("CustMobile", rs.getString("remitter_msisdn"));

			details.put("Amount", "Rs. " + df.format(rs.getFloat("amount") / 100));
			details.put("Charges", "Rs. " + df.format(rs.getFloat("charge") / 100));

			return details;
		} catch (SQLException sqe) {
			sqe.printStackTrace();
		}
		return null;

	}

	public HashMap<String, String> getTransactionRefundDetails(String remitterMobileNo, String novopayReferenceNo) {
		HashMap<String, String> details = new LinkedHashMap<String, String>();
		try {
			conn = createConnection(configProperties.get("npRemittance"));
			String query = "SELECT * FROM `remittance_outward_table` WHERE remitter_msisdn = '" + remitterMobileNo
					+ "' AND `payment_ref_code` = '" + novopayReferenceNo + "'";
			System.out.println(query);

			stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery(query);
			DecimalFormat df = new DecimalFormat("#.00");
			rs.last();

			details.put("transactionType", rs.getString("remittance_type"));
			details.put("accountNumber", rs.getString("beneficiary_acc_num"));
			details.put("beneficiaryName", rs.getString("beneficiary_name"));
			details.put("transactionAmount", df.format(rs.getFloat("amount") / 100));
			details.put("transactionCharge", df.format(rs.getFloat("charge") / 100));
			details.put("partnerReferenceNo", rs.getString("bank_ref_code"));
			details.put("bankName", rs.getString("beneficiary_bank"));
			details.put("transactionCurrency", rs.getString("currency"));
			details.put("transactionStatus", rs.getString("status"));
			details.put("novopayReferenceNo", rs.getString("payment_ref_code"));
			// details.put("transactionNarration",
			// rs.getString("rejection_remarks"));
			details.put("transactionDate", rs.getString("created_on"));
			return details;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public HashMap<String, String> returnIFSCDetails(String IFSCCode) {

		String address = null, branchName = null, bankName = null, city = null;
		HashMap<String, String> bankDetails = new HashMap<String, String>();
		try {
			String query1 = "SELECT * FROM `ifsc_master_new` WHERE `ifsc_code` = '" + IFSCCode + "'";
			ResultSet rs = stmt.executeQuery(query1);

			while (rs.next()) {

				bankDetails.put("address", rs.getString("address"));
				bankDetails.put("branchName", rs.getString("branch"));
				bankDetails.put("bankName", rs.getString("bank"));
				bankDetails.put("city", rs.getString("city"));
				bankDetails.put("ifscCode", rs.getString("ifsc_code"));
			}
			return bankDetails;
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! returnIFSCDetails failed..!");
			sqe.printStackTrace();
		}
		Reporter.log("FAILURE..! Exception found while returnIFSCDetails from np_actor DB..!");
		return null;
	}

	public HashMap<String, String> returnIFSCCode(String bankName, String state, String district, String branch) {

		HashMap<String, String> bankDetails = new HashMap<String, String>();
		try {
			String query1 = "SELECT * FROM `ifsc_master_new` WHERE `bank` LIKE '%" + bankName
					+ "%' AND `branch` LIKE '%" + branch + "%' AND `state` LIKE '%" + state
					+ "%' AND `district` LIKE '%" + district + "%'";

			ResultSet rs = stmt.executeQuery(query1);

			while (rs.next()) {

				bankDetails.put("address", rs.getString("address"));
				bankDetails.put("branchName", rs.getString("branch"));
				bankDetails.put("bankName", rs.getString("bank"));
				bankDetails.put("city", rs.getString("city"));
				bankDetails.put("ifscCode", rs.getString("ifsc_code"));
			}
			return bankDetails;
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! returnIFSCCode failed..!");
			sqe.printStackTrace();
		}
		Reporter.log("FAILURE..! Exception found while returnIFSCCode from np_actor DB..!");
		return null;
	}

	public long getNextId(Connection conn) throws ClassNotFoundException, SQLException {

		PreparedStatement ps = null;
		ResultSet rs = null;
		long id;
		String sQuery = "SELECT MAX(`id`) FROM auto_es_details";
		ps = conn.prepareStatement(sQuery);
		rs = ps.executeQuery();
		rs.next();
		if (null != rs.getString(1))
			id = Long.parseLong(rs.getString(1)) + 1;
		else
			id = 1;
		return id;
	}

	public void insertTestResultsToAutomationElasticSearchDB(Connection conn, String productName, String apiName,
			String executionDate, String totalExec, String totalPass, String totalFail, String totalSkip)
			throws ClassNotFoundException, SQLException {

		PreparedStatement ps = null;
		long id = getNextId(conn);
		try {
			String query1 = "INSERT INTO `auto_es_details` "
					+ "(`id`, `product_name`, `api_name`, `execution_date`, `tests_executed`, `tests_passed`, `tests_failed`, `tests_skipped`)"
					+ "VALUES(?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(query1);
			ps.setString(1, String.valueOf(id));
			ps.setString(2, productName);
			ps.setString(3, apiName);
			ps.setString(4, executionDate);
			ps.setString(5, totalExec);
			ps.setString(6, totalPass);
			ps.setString(7, totalFail);
			ps.setString(8, totalSkip);

			ps.executeUpdate();
		} catch (SQLException sqe) {
			System.out.println("Error connecting DB!! returnIFSCCode failed..!");
			sqe.printStackTrace();
		}
		Reporter.log("FAILURE..! Exception found while returnIFSCCode from np_actor DB..!");
	}

	public void resetUpgradeWalletRequest(String handleValue) {

		try {
			conn = createConnection(configProperties.get("npActor"));
			String query = "SELECT `customer_id` FROM `customer_handle` WHERE handle_value = '" + handleValue + "'";
			System.out.println(query);
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) {
				String customer_id = rs.getString("customer_id");
				query = "UPDATE `s_customer` SET `customer_status` = 'REJECTED_BY_OPS' WHERE `customer_id` = '"
						+ customer_id + "'";
				System.out.println(query);
				stmt = conn.createStatement();
				stmt.executeUpdate(query);
				query = "UPDATE `customer_onboarding_details` SET `customer_kyc_stage` = 'REJECTED_BY_OPS' WHERE `customer_id` = '"
						+ customer_id + "'";
				stmt = conn.createStatement();
				stmt.executeUpdate(query);
				System.out.println(query);
			} else {
				System.out.println("No customer found");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public HashMap<String, String> getAgentsWalletRefNo(String mobileNumber) {

		try {
			HashMap<String, String> walletRefNumber = new HashMap<String, String>();
			conn = createConnection(configProperties.get("master"));
			String query = "SELECT attr_key,attr_value FROM master.organization_attribute WHERE attr_key IN ('WALLET_ACCOUNT_NUMBER','OTC_MERCHANT_WALLET_ACCOUNT_NUMBER','CASH_OUT_WALLET_ACCOUNT_NUMBER')"
					+ "AND orgnization_id = (SELECT id FROM organization WHERE CODE = (SELECT org_code FROM organization_devices WHERE msisdn = '"
					+ mobileNumber + "'));";
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (rs.getString("attr_key").equals("OTC_MERCHANT_WALLET_ACCOUNT_NUMBER"))
					walletRefNumber.put("merchantWalletRefNo", rs.getString("attr_value"));
				else if (rs.getString("attr_key").equals("CASH_OUT_WALLET_ACCOUNT_NUMBER"))
					walletRefNumber.put("cashOutWalletRefNo", rs.getString("attr_value"));
				else if (rs.getString("attr_key").equals("WALLET_ACCOUNT_NUMBER"))
					walletRefNumber.put("agentWalletRefNo", rs.getString("attr_value"));
			}
			System.out.println(walletRefNumber);
			return walletRefNumber;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getTenantServiceSchemaName(String serviceName, String tenantCode) {
		try {
			String query = "SELECT `schema_name` FROM `tenant_service_db_config` WHERE `tenant_service_id` =(SELECT `id` FROM `tenant_service` WHERE `tenant_master_id`=(SELECT `id` FROM `tenant_master` WHERE `code`='"
					+ tenantCode + "') AND `service_master_id`=(SELECT `id` FROM`service_master` WHERE `name`='"
					+ serviceName + "'))";
			conn = createConnection(configProperties.get("platformMaster"));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("schema_name");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getParentOfficeId(String serviceName, String officeName, HashMap<String, String> usrData) {
		try {
			String schemaName;
			String query = "SELECT `id` FROM `office` WHERE NAME ='" + officeName + "';";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			System.out.println(query);
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("id");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getAddressId(String serviceName, HashMap<String, String> usrData, String officeId) {
		try {
			String schemaName;
			String query = "SELECT `address_id` FROM `office_address` WHERE `office_id`='" + officeId + "';";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			System.out.println(configProperties.get(usrData.get("tenant_code") + serviceName));
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("address_id");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getContactId(String serviceName, HashMap<String, String> usrData, String officeId) {
		try {
			String schemaName;
			String query = "SELECT `contact_id` FROM `office` WHERE `id`=" + officeId;
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getUserId(String userName, HashMap<String, String> usrData) {
		try {
			String serviceName = "ACTOR";
			String schemaName;
			String query = "SELECT `user_id` FROM `user_handle` WHERE `value` ='"+userName+"' AND handle_type_id = '3';";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("user_id");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String disablingApprovalForGLCreation(String usecaseNumber, HashMap<String, String> usrData,int makervalue) {
		try{
			String serviceName = "Authorisation";
			String schemaName="auto_authorization";
			String query ="UPDATE  `usecase_master` SET `maker_checker_enabled`="+makervalue+" WHERE `id`="+usecaseNumber+";";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				
			}
			
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			int rs = stmt.executeUpdate(query);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String disablingApprovalForGLUpdate(String id, HashMap<String, String> usrData,int makervalue) {
		try{
			String serviceName = "Authorisation";
			String schemaName="auto_authorization";
			String query ="UPDATE  `usecase_master` SET `maker_checker_enabled`="+makervalue+" WHERE `id`="+id+";";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				
			}
			
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			int rs = stmt.executeUpdate(query);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**enabling maker checker value for the office case
	 * 
	 * @param usecaseNumber
	 * @param usrData
	 * @param makervalue
	 * @return
	 */
	
	public String makercheckerenablingdisabling(String usecaseNumber, HashMap<String, String> usrData,int makervalue) {
		try{
		/*	Jedis jedis = new Jedis("192.168.150.17", 6379);
          //jedis.auth("N0V0P@sswordCb$");
			System.out.println("Connected to Redis");
			   
			  String Key="idfcp_"+usecaseNumber;
			 System.out.println(Key);
			 Long keyValue=jedis.del(Key);
			System.out.println(keyValue);
		    jedis.close();*/
			Jedis jedis = new Jedis("192.168.150.17", 6379);
			jedis.auth("N0V0P@sswordCb$");
			System.out.println("Connected to Jedis");
			String key = "idfcp_" + usecaseNumber;
			System.out.println(key);
			  
			String serviceName = "Authorisation";
			String schemaName="idfcp_authorization";
			String query ="UPDATE `usecase`SET `maker_checker_enabled`='"+makervalue+"' WHERE `code`='"+usecaseNumber+"';";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				}
			
		    conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			int rs = stmt.executeUpdate(query);
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getVersionNumber(String code, HashMap<String, String> usrData){
		try{
			String serviceName = "Accounting";
			String schemaName="auto_accounting";
			String query ="SELECT `version` FROM `base_interest_master` WHERE `code`='"+code+"' ORDER BY `version` DESC LIMIT 1";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				}
			
		    conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
		    stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery(query);
			rs.next();
		return rs.getString("version");
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public String getIdFordeletion(HashMap<String, String> usrData,String tableName ){
		try{
			String serviceName = "Accounting";
			String schemaName="auto_accounting";
			String query ="SELECT `id` FROM  `"+ tableName +"` WHERE `is_deleted`='0' ORDER BY id DESC LIMIT 1";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				}
			
		    conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
		    stmt = conn.createStatement();
		    ResultSet rs = stmt.executeQuery(query);
			rs.next();
		return rs.getString("id");
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getGLapplicationId(String usecase,String createdBy,String status,HashMap<String, String> usrData){
		try{
			String serviceName="approval";
			String schemaName="idfcp_approval";
			String query="SELECT id FROM `application` WHERE `usecase`='"+usecase+"' AND `created_by`='"+createdBy+"' AND `status`='"+status+"' LIMIT 1;";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
		return rs.getString("id");
			}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public String getRoleapplicationId(String usecase,String createdBy,String status,HashMap<String, String> usrData){
		try{
			String serviceName="approval";
			String schemaName="idfcp_approval";
			String identifier = getvalueFromTestDataIni("code");
			String query= "SELECT `id` FROM `application` WHERE `identifier` = " + "'" + identifier + "'";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
		return rs.getString("id");
			}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public String getTaxGroupId(HashMap<String, String> usrData,String code){
		
		try{
			String serviceName="Accounting";
			String schemaName;
			String query="SELECT id FROM `tax_group` WHERE `is_deleted`='0' AND `code`='"+code+"' LIMIT 1;";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
		return rs.getString("id");
		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
		
	}
	

	public String getOfficeName(String serviceName, String userName, HashMap<String, String> usrData) {
		try {
			String schemaName;
			String query = "SELECT `name` FROM `office` WHERE `id` IN "
					+ "(SELECT `office_id` FROM `user_office` WHERE `user_id` IN "
					+ "(SELECT `id` FROM `user` WHERE `first_name` LIKE '" + userName + "'));";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("name");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String getVersion(String code,HashMap<String, String> usrData){
		try{
			
			
			 String serviceName="ACCOUNTING";
			 String schemaName;
			 String query="SELECT `version` FROM `savings_product` WHERE `product_id`IN (SELECT `id` FROM `product` WHERE `code`='"+code+"') ORDER BY VERSION DESC LIMIT 1";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("version");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
		
	}
	

	public String getGroupId(String serviceName, String groupCode, HashMap<String, String> usrData) {
		try {
			String schemaName;
			String query = "SELECT `id` FROM `rbac_group` WHERE `code` = '" + groupCode + "';";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("id");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getGroupCode(String serviceName, String groupName, HashMap<String, String> usrData) {
		try {
			String schemaName;
			String query = "SELECT `code` FROM `rbac_group` WHERE `display_name` = '" + groupName + "';";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("code");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getRoleid(String roleName, HashMap<String, String> usrData) {
		try {
			String serviceName = "AUTHORIZATION";
			String schemaName;
			String query = "SELECT `id` FROM `rbac_role` WHERE `display_name` ='" + roleName + "';";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("id");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getPermissionCode(String serviceName, String roleName, HashMap<String, String> usrData) {
		try {
			String schemaName;
			String query = "SELECT `code` FROM `rbac_permission` WHERE `entity_id` IN (SELECT `permission_id` FROM `rbac_role_permission` WHERE role_id IN (SELECT `id` FROM `rbac_role` WHERE `display_name` = '"
					+ roleName + "'));";
			System.out.println(query);
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("code");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public List<String> getAllPermissionCodes(String serviceName, HashMap<String, String> usrData) {
		
		String query = "Select CODE from `permission`";
		
		conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	

	public String getID(String serviceName, HashMap<String, String> usrData, String name,String tableName) {
		try {
			String schemaName;
			String query = "SELECT `id` FROM `"+ tableName + "` WHERE `code`='" + name + "'";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("id");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	public String getIdentifier(String applicationId, HashMap<String, String> usrData){
		try {
			String serviceName="Approval";
			String schemaName;
			String query = "SELECT `identifier` FROM `application` WHERE `id`='" + applicationId + "'";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("identifier");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
		
	}

	
	
	public String getInternalAccountDefinitionID(HashMap<String, String> usrData, String internalAccDefName) {
		try {
			String serviceName = "ACCOUNTING";
			String schemaName;
			String query = "SELECT `id` FROM `internal_account_definition` WHERE `account_code`='"+internalAccDefName+"'";
			
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("id");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getInternalAccountTemplateID(HashMap<String, String> usrData, String internalAccTempName) {
		try {
			String serviceName = "ACCOUNTING";
			String schemaName;
			String query = "SELECT `id` FROM `internal_account_template` WHERE `name`='"+internalAccTempName+"'";
			if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("id");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	//gateway
	
	public String returnTenant(HashMap<String,String> usrData){
		
		return !(usrData.get("tenant_code").equalsIgnoreCase("demo"))?"_"+usrData.get("tenant_code")+"_":"_";
	}

	public String getSessionToken(HashMap<String, String> usrData) {
		try {
			String serviceName = "ACTOR";
			String service = getvalueFromIni("actor");
			String schemaName=getvalueFromIni("gateway");
			
			String query = "SELECT * FROM  `"+schemaName+"`.`session`  WHERE  `"+schemaName+"`.`session`.`user_id` = (SELECT `user_id` FROM `" + service +"`.`user_handle` WHERE `value`='"
					+ usrData.get("user_handle_value") + "') ORDER BY `expiry` DESC";
			System.out.println(query);
			/*if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
				schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				configProperties.put(usrData.get("tenant_code") + serviceName, schemaName);
				addConfigToIni(usrData.get("tenant_code") + serviceName, schemaName);
			}*/
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-mm-dd HH:mm:ss");
			DateTime sessionExpiry = formatter.parseDateTime(rs.getDate("expiry") + " " + rs.getTime("expiry"));
			DateTime currDateTime = formatter.parseDateTime(getTodaysDate("yyyy-mm-dd HH:mm:ss"));
			if (sessionExpiry.isBefore(currDateTime.plusHours(1))) {
				query = "UPDATE `"+schemaName+"`.`session` SET `expiry`= NOW()+INTERVAL 1 HOUR WHERE `token`='"
						+ rs.getString("token") + "'";
				System.out.println(query);
				stmt = conn.createStatement();
				stmt.executeUpdate(query);
			}
			return rs.getString("token");

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	
	
	
	public String getAuthValue(String userName, HashMap<String, String> usrData) {
		try {
			String serviceName = "ACTOR";
			String schemaName=getvalueFromIni("actor");
			String query = "SELECT `value` FROM `user_auth` WHERE `user_id` IN (SELECT `id` FROM `user` WHERE `first_name` = '"+userName+"');";
		if (null == (configProperties.get(usrData.get("tenant_code") + serviceName))) {
			schemaName = getTenantServiceSchemaName(serviceName, usrData.get("tenant_code"));
				
			}
			conn = createConnection(configProperties.get(usrData.get("tenant_code") + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			return rs.getString("value");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void assignPermissionToAdmin(String tenantCode) {
		try {
			String serviceName = "AUTHORIZATION";
			String schemaName;
			
			schemaName = getTenantServiceSchemaName(serviceName, tenantCode);
			configProperties.put(tenantCode + serviceName, schemaName);
			String query = "(SELECT 1, rp.id FROM  rbac_permission rp LEFT JOIN rbac_role_permission rrp ON rp.id = rrp.permission_id WHERE rrp.id IS NULL);";
			conn = createConnection(configProperties.get(tenantCode + serviceName));
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			//Array permissionId = rs.getArray("id");
			//System.out.println(permissionId);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		
	}
}
