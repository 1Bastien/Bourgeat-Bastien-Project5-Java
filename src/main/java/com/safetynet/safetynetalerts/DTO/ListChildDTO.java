package com.safetynet.safetynetalerts.DTO;

import java.util.List;

public class ListChildDTO {

	private List<ChildDTO> children;

	public List<ChildDTO> getChildren() {
		return children;
	}

	public void setChildren(List<ChildDTO> children) {
		this.children = children;
	}

	public static class ChildDTO {

		private String firstName;

		private String lastName;

		private int age;

		private List<OtherPersonDTO> otherMembers;

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public List<OtherPersonDTO> getOtherMembers() {
			return otherMembers;
		}

		public void setOtherMembers(List<OtherPersonDTO> otherMembers) {
			this.otherMembers = otherMembers;
		}

	}

	public static class OtherPersonDTO {

		private String firstName;

		private String lastName;

		private int age;

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

	}
}
