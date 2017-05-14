package net.winroad.wrdoclet.data;

public class ModificationRecord {
	/*
	 * The author who did this modification. Corresponds to @author.
	 */
	private String modifier;
	/*
	 * Describes when this functionality has first existed. Corresponds to
	 * @version.
	 */
	private String version;
	/*
	 * Describes the modification. Corresponds to @memo.
	 */
	private String memo;

	/*
	 * @return Whether this record is modified on specified version. If no
	 * version specified, returns true.
	 */
	public boolean isModifiedOnVersion(String version) {
		if (version == null || version.isEmpty()) {
			return true;
		}
		return version.equalsIgnoreCase(this.version);
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
}
