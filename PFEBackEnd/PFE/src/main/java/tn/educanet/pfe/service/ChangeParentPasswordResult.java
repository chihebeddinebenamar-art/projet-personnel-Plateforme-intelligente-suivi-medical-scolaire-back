package tn.educanet.pfe.service;

public record ChangeParentPasswordResult(boolean ok, String message) {

	public static ChangeParentPasswordResult success() {
		return new ChangeParentPasswordResult(true, null);
	}

	public static ChangeParentPasswordResult fail(String message) {
		return new ChangeParentPasswordResult(false, message);
	}
}
