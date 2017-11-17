package com.dal;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

public class ProcedureTableModel implements Serializable {

	private static final long serialVersionUID = 1L;
	Result result[] = null;

	int resultSize = 0;
	int inSize = 0;
	int outSize = 0;

	String[] inputs;
	String procedureName = "";
	String packageName = "";

	String procedurePrototype = "";

	Connection db;
	Statement statement;
	String currentURL;
	String hata = "";

	public ProcedureTableModel() {
	}

	private void clearData() {
		result = null;
		statement = null;
		currentURL = null;
		hata = null;

	}

	private void spStringPrepare(ProcedureTableModel t) {
		String param = "", virgul = ",";
		String nls = "  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';";
		String soru = " ? ";
		for (int i = 0; i < t.result[0].getRowCount(); i++) {
			if (i == t.result[0].getRowCount() - 1)
				virgul = "";
			param = param + soru + virgul;
		}
		String spPackage = t.packageName.length() > 0 ? t.packageName + "."
				: "";
		t.procedurePrototype = "begin " + nls + " " + spPackage
				+ this.procedureName + " ( " + param + ") ; end;";
	}

	protected ProcInfo spPropertiesSetting(ProcedureTableModel t,
			ProcInfo procInfo) {
		// ProcInfo procInfo = new ProcInfo();
		// /procInfo.
		// ProcInfo.getInstance().params.clear();
		String sqlStr = procInfo.sql;
		// ProcInfo.getInstance().sql;
		sqlStr = sqlStr + "'" + t.procedureName + "' ";
		if (t.packageName.length() > 0)
			sqlStr = sqlStr + " AND PACKAGE_NAME='" + t.packageName + "'";

		sqlStr = sqlStr + procInfo.ord;

		DBFunc db = new DBFunc();
		db.query(sqlStr);

		t.setResultSetData(db.rs);

		db.close();

		for (int i = 0; i < t.result[0].cache.size(); i++) {
			String[] str = (String[]) t.result[0].cache.get(i);
			ProcedureProperties pi = new ProcedureProperties();
			pi.setParameterName(str[0] + "");
			pi.setPosition(str[1] + "");
			pi.setParamType(str[2] + "");
			pi.setInOut(str[3] + "");
			if (str[3].equals(ProcInfo.IN))
				inSize++;
			if (str[3].equals(ProcInfo.OUT))
				outSize++;
			procInfo.params.put(i + 1, pi);

			// ProcInfo.getInstance().params.put(i + 1, pi);
			// params.put(t.values[i][1],pi );
		}
		return procInfo;
		// milliseconds1 = Calendar.getInstance().getTimeInMillis();
		// System.out.println("spPropertiesSetting-5:"
		// + (milliseconds1 - milliseconds2));
	}

	private void setHostURL(String url) {
		if (url.equals(currentURL)) {

			return;
		}
		closeDB();
		initDB(url);
		currentURL = url;
	}

	private void setResult(Result result) {
		Result[] temp;
		resultSize++;
		if (this.result == null) {
			// first input settings
			this.result = new Result[resultSize];
			this.result[resultSize - 1] = result;

		} else {
			//
			int i = getResultSize();
			temp = new Result[resultSize];
			for (int j = 0; j < this.result.length; j++) {
				temp[j] = this.result[j];
			}
			temp[resultSize - 1] = result;
			this.result = temp;
			// System.out.println("QueryTableModel.setResult()");
		}
	}

	public int getResultSize() {
		return result.length;
	}

	public Result[] getResult() {
		return result;
	}

	private Result getValueAt(int index) {
		return result[index];

	}

	// All the real work happens here; in a real application,
	// we'd probably perform the query in a separate thread.
	private void setQuery(String q, QueryTableModel t) {
		// result = new Result();
		Result result = new Result();
		try {
			// Execute the query and store the result set and its metadata
			ResultSet rs = t.statement.executeQuery(q);
			ResultSetMetaData meta = rs.getMetaData();
			result.colCount = meta.getColumnCount();

			result.headers = new String[result.colCount];
			for (int h = 1; h <= result.colCount; h++) {
				result.headers[h - 1] = meta.getColumnName(h);
			}

			while (rs.next()) {
				String[] record = new String[result.colCount];
				for (int i = 0; i < result.colCount; i++) {
					record[i] = rs.getString(i + 1);
				}
				result.cache.addElement(record);
				// result.cache.add(record);
			}
			setResult(result);//
		} catch (Exception e) {
			result.cache = new Vector(); // blank it out and keep going.
			// result.cache = new ArrayList();
			e.printStackTrace();
		}
	}

	public void initDB(String url) {
		try {
			db = ConnectionManager.getConnection();
			statement = db.createStatement();
		} catch (Exception e) {
			System.out.println("Could not initialize the database.");
			e.printStackTrace();
		}
	}

	private void closeDB() {
		try {
			if (statement != null) {
				statement.close();
			}
			if (db != null) {
				db.close();
			}
		} catch (Exception e) {
			System.out.println("Could not close the current connection.");
			e.printStackTrace();
		}
	}

	private void setResultSetData(ResultSet rs) {
		ResultSetMetaData meta = null;
		Result result = new Result();

		try {
			meta = rs.getMetaData();
			result.colCount = meta.getColumnCount();
			result.headers = new String[result.colCount];
			for (int h = 1; h <= result.colCount; h++) {
				result.headers[h - 1] = meta.getColumnName(h);
			}
			while (rs.next()) {
				String[] record = new String[result.colCount];
				for (int i = 0; i < result.colCount; i++) {
					record[i] = rs.getString(i + 1);
				}
				result.cache.addElement(record);
				// murat eren
				// result.bilgiDoldur();
				// result.cache.add(record);
			}
			setResult(result);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void setResultSingleData(String value) {
		Result result = new Result();
		try {
			result.colCount = 1;
			result.headers = new String[] { "Single Value" };
			// result.cache.addElement(value);
			String[] val = new String[1];
			val[0] = value;
			result.cache.add(val);
			// murat eren
			// result.bilgiDoldur();
			// resultSize++;
			setResult(result);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private int parametreControl(Object[] o) {

		if (this.inSize != o.length)
			return -1;
		return 0;
	}

	private void procedureExecuter(ProcedureTableModel t, ProcInfo procInfo, Bug bug) {
		// t.initDB("");
		OracleCallableStatement cStmt;
		ResultSet rset = null, rs = null;
		// if(parametreControl(t.inputs)==-1){
		// t.hata="Parametre sayisi uygun değil.";
		// return;
		// }
		// System.out.println("spCaller-1:" + Calendar.getInstance().getTime());
		System.out.println(t.procedurePrototype);
		try {
			cStmt = (OracleCallableStatement) t.db
					.prepareCall(t.procedurePrototype);
			// System.out
			// .println("spCaller-2:" + Calendar.getInstance().getTime());
			// for (int i = 1; i <= ProcInfo.getInstance().params.size(); i++) {
			for (int i = 1; i <= procInfo.params.size(); i++) {
				ProcedureProperties pi = (ProcedureProperties) procInfo.params
						.get(i);
				if (pi.inOut.equals("IN")) {
					if (t.inputs[i - 1] != null) {
						// System.out
						// .println(""+i+"|"+t.inputs[i - 1]);
						cStmt.setString(i, t.inputs[i - 1].toString());
					} else {
						System.out.println("" + i + "|null");
						cStmt.setString(i, null);
					}
				} else if (pi.inOut.equals("OUT")) {

					if (pi.getParamType().equals(procInfo.CURSOR)) {
						// System.out
						// .println(""+i+"|cursor");
						cStmt.registerOutParameter(i, OracleTypes.CURSOR);
					} else {
						// System.out
						// .println(""+i+"|varchar");
						cStmt.registerOutParameter(i, OracleTypes.VARCHAR);
					}

				}
			}
			// System.out
			// .println("spCaller-3:" + Calendar.getInstance().getTime());
			cStmt.execute();
			// System.out
			// .println("spCaller-5:" + Calendar.getInstance().getTime());
			int outIndex = 0;
			// for (int i = 1; i <= ProcInfo.getInstance().params.size(); i++) {
			for (int i = 1; i <= procInfo.params.size(); i++) {
				ProcedureProperties pi = (ProcedureProperties) procInfo.params
						.get(i);
				if (pi.inOut.equals("OUT")) {
					outIndex = Integer.parseInt(pi.getPosition().toString()
							.trim());
					if (pi.getParamType().equals(procInfo.CURSOR)) {

						try {
							System.out
									.println("ProcedureTableModel.procedureExecuter():st:1: "
											+ t.inputs[0] + ":" + outIndex);
							rset = ((OracleCallableStatement) cStmt)
									.getCursor(outIndex);
							System.out
									.println("ProcedureTableModel.procedureExecuter():st:2: "
											+ t.inputs[0] + ":" + outIndex);

							((OracleCallableStatement) cStmt)
									.getCursor(outIndex);
							t.setResultSetData(rset);
							System.out
									.println("ProcedureTableModel.procedureExecuter():st:3: "
											+ t.inputs[0] + ":" + outIndex);

						} catch (Exception e) {
							// TODO: handle exception
							System.out
									.println("ProcedureTableModel.procedureExecuter():inner exp:"
											+ t.inputs[0] + ": ind:" + outIndex);
							e.printStackTrace();
						}
					} else {
						String value = "";
						System.out
								.println("ProcedureTableModel.procedureExecuter():st:11: "
										+ t.inputs[0] + ":" + outIndex);

						value = ((OracleCallableStatement) cStmt)
								.getString(outIndex);
						System.out
								.println("ProcedureTableModel.procedureExecuter():st:12: "
										+ t.inputs[0] + ":" + outIndex);

						t.setResultSingleData(value);
						System.out
								.println("ProcedureTableModel.procedureExecuter():st:13: "
										+ t.inputs[0] + ":" + outIndex);
					}

				}
			}
			// System.out
			// .println("spCaller-6:" + Calendar.getInstance().getTime());
		} catch (SQLException e) {
			if(bug.equals(null)|| bug== null)  bug= new Bug();
			
			bug.setCode(e.getErrorCode()+"");
			bug.setDescription(e.getMessage());
			System.out
					.println("ProcedureTableModel.procedureExecuter():genel exp:"
							+ t.inputs[0]);
			// TODO
			e.printStackTrace();
		}
	}

	public ProcedureTableModel procedureExecuter(DataCallerInput callerInput,
			ProcedureTableModel procedureTableModel, ProcInfo procInfo, Bug bug) {

		procedureTableModel.inputs = callerInput.inputs;
		procedureTableModel.procedureName = callerInput.procedureName;
		procedureTableModel.packageName = callerInput.packageName;
		procedureTableModel.initDB("");
		procInfo = procedureTableModel.spPropertiesSetting(procedureTableModel,
				procInfo);

		procedureTableModel.spStringPrepare(procedureTableModel);
		procedureTableModel.procedureExecuter(procedureTableModel, procInfo, bug);

		procedureTableModel.closeDB();
		// System.out.println("result:" +
		// procedureTableModel.getResult().length);
		return procedureTableModel;
	}

	public int getInSize() {
		return inSize;
	}

	public void setInSize(int inSize) {
		this.inSize = inSize;
	}

	public int getOutSize() {
		return this.outSize;
	}

	public void setOutSize(int outSize) {
		this.outSize = outSize;
	}

	public String[] getInputs() {
		return inputs;
	}

	public void setInputs(String[] inputs) {
		this.inputs = inputs;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getProcedurePrototype() {
		return procedurePrototype;
	}

	public void setProcedurePrototype(String procedurePrototype) {
		this.procedurePrototype = procedurePrototype;
	}

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public String getHata() {
		return hata;
	}

	public void setHata(String hata) {
		this.hata = hata;
	}

	public void setResult(Result[] result) {
		this.result = result;
	}

	public void setResultSize(int resultSize) {
		this.resultSize = resultSize;
	}

}
