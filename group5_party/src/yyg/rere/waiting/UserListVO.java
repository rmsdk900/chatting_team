package yyg.rere.waiting;

public class UserListVO {
	int num;
	String name;
	
	public UserListVO() {}

	public UserListVO(int num, String name) {
		this.num = num;
		this.name = name;
	}

	public final int getNum() {
		return num;
	}

	public final void setNum(int num) {
		this.num = num;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}
	
	
	
}
