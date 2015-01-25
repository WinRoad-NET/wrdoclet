package net.winroad.wrdoclet.data;

public class ModificationRecord {
	/*
	 * The author who did this modification. Corresponds to @author.
	 */
	private String modifier;
	/*
	 * Describes when this functionality has first existed. 
	 * Corresponds to @since.
	 */
	private String releaseVersion;
	/*
	 * Describes the modification. Corresponds to @wr.memo.
	 */
	private String memo;

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getReleaseVersion() {
		return releaseVersion;
	}

	public void setReleaseVersion(String releaseVersion) {
		this.releaseVersion = releaseVersion;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
}
