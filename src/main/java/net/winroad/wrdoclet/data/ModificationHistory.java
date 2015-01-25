package net.winroad.wrdoclet.data;

import java.util.LinkedList;
import java.util.List;

public class ModificationHistory {
	private List<ModificationRecord> modificationRecordList = new LinkedList<ModificationRecord>();

	public List<ModificationRecord> getModificationRecordList() {
		return modificationRecordList;
	}

	public void AddModificationRecord(ModificationRecord record) {
		this.modificationRecordList.add(record);
	}
	
	public void AddModificationRecords(LinkedList<ModificationRecord> record) {
		this.modificationRecordList.addAll(record);
	}
}
