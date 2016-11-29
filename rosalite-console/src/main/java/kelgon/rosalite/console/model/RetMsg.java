package kelgon.rosalite.console.model;

public class RetMsg {
	public RetMsg(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	public RetMsg(String code, String message, Object obj) {
		super();
		this.code = code;
		this.message = message;
		this.obj = obj;
	}
	private String code;
	private String message;
	private Object obj;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
}
