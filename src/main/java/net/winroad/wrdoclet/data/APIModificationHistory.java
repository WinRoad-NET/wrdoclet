package net.winroad.wrdoclet.data;

import java.util.List;

public class APIModificationHistory {
	private List<APIModificationRecord> modificationRecordList;

	public List<APIModificationRecord> getModificationRecordList() {
		return modificationRecordList;
	}

	public void setModificationRecordList(
			List<APIModificationRecord> modificationRecordList) {
		this.modificationRecordList = modificationRecordList;
	}

}
