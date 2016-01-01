package net.winroad.wrdoclet.taglets;

import java.util.LinkedList;

import net.winroad.wrdoclet.data.APIParameter;
import net.winroad.wrdoclet.data.ModificationHistory;
import net.winroad.wrdoclet.data.ModificationRecord;
import net.winroad.wrdoclet.data.ParameterOccurs;
import net.winroad.wrdoclet.utils.Logger;
import net.winroad.wrdoclet.utils.LoggerFactory;

import org.springframework.util.StringUtils;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

public abstract class AbstractWRParamTaglet implements Taglet {
	static Logger logger = LoggerFactory.getLogger(AbstractWRParamTaglet.class);;

	@Override
	public boolean inConstructor() {
		return false;
	}

	@Override
	public boolean inField() {
		return false;
	}

	@Override
	public boolean inMethod() {
		return true;
	}

	@Override
	public boolean inOverview() {
		return false;
	}

	@Override
	public boolean inPackage() {
		return false;
	}

	@Override
	public boolean inType() {
		return false;
	}

	@Override
	public boolean isInlineTag() {
		return false;
	}

	@Override
	public String toString(Tag tag) {
		return toString(new Tag[] { tag });
	}

	@Override
	public String toString(Tag[] tags) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < tags.length - 1; i++) {
			result.append(tags[i].text());
			result.append(',');
		}
		if (tags.length > 0) {
			result.append(tags[tags.length - 1].text());
		}
		return result.toString();
	}

	public static APIParameter parse(String comment) {
		APIParameter result = new APIParameter();
		String[] data = comment.split("\\|\\|");
		for (int i = 0; i < data.length; i++) {
			switch (i) {
			case 0:
				result.setName(data[i]);
				break;
			case 1:
				result.setType(data[i]);
				break;
			case 2:
				result.setDescription(data[i]);
				break;
			case 3:
				if ("是".equals(StringUtils.trimAllWhitespace(data[i]))
						|| "Yes".equalsIgnoreCase(StringUtils
								.trimAllWhitespace(data[i]))
						|| "Y".equalsIgnoreCase(StringUtils
								.trimAllWhitespace(data[i]))) {
					result.setParameterOccurs(ParameterOccurs.REQUIRED);
				} else if ("否".equals(StringUtils.trimAllWhitespace(data[i]))
						|| "No".equalsIgnoreCase(StringUtils
								.trimAllWhitespace(data[i]))
						|| "N".equalsIgnoreCase(StringUtils
								.trimAllWhitespace(data[i]))) {
					result.setParameterOccurs(ParameterOccurs.OPTIONAL);
				} else {
					result.setParameterOccurs(ParameterOccurs.DEPENDS);
				}
				break;
			case 4:
				LinkedList<ModificationRecord> list = new LinkedList<ModificationRecord>();
				if (data[i] != null) {
					String[] hisArr = data[i].split("\\|");
					for (int j = 0; j < hisArr.length - (hisArr.length % 3); j += 3) {
						ModificationRecord r = new ModificationRecord();
						r.setModifier(hisArr[j]);
						r.setMemo(hisArr[j+1]);
						r.setVersion(hisArr[j+2]);
						list.add(r);
					}
					if(hisArr.length % 3 != 0) {
						logger.warn("Modification history format may not be correct: " + comment);
					}
				}
				ModificationHistory history = new ModificationHistory();
				history.addModificationRecords(list);
				result.setHistory(history);
				break;
			default:
				logger.warn("Unkown customized parameter comment format: " + comment);
				break;
			}
		}
		return result;
	}
}
