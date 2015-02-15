package net.winroad.wrdoclet;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeOptionalMemberWriter;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeRequiredMemberWriter;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeWriter;
import com.sun.tools.doclets.internal.toolkit.ClassWriter;
import com.sun.tools.doclets.internal.toolkit.ConstantsSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.ConstructorWriter;
import com.sun.tools.doclets.internal.toolkit.EnumConstantWriter;
import com.sun.tools.doclets.internal.toolkit.FieldWriter;
import com.sun.tools.doclets.internal.toolkit.MemberSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.MethodWriter;
import com.sun.tools.doclets.internal.toolkit.PackageSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.PropertyWriter;
import com.sun.tools.doclets.internal.toolkit.SerializedFormWriter;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;

public class WriterFactoryImpl implements WRWriterFactory {

	public FreemarkerWriter getFreemarkerWriter() {
		return new FreemarkerWriter();
	}
	
	public AnnotationTypeOptionalMemberWriter getAnnotationTypeOptionalMemberWriter(
			AnnotationTypeWriter arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public AnnotationTypeRequiredMemberWriter getAnnotationTypeRequiredMemberWriter(
			AnnotationTypeWriter arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public AnnotationTypeWriter getAnnotationTypeWriter(AnnotationTypeDoc arg0,
			Type arg1, Type arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public ClassWriter getClassWriter(ClassDoc arg0, ClassDoc arg1,
			ClassDoc arg2, ClassTree arg3) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public ConstantsSummaryWriter getConstantsSummaryWriter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public ConstructorWriter getConstructorWriter(ClassWriter arg0)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public EnumConstantWriter getEnumConstantWriter(ClassWriter arg0)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public FieldWriter getFieldWriter(ClassWriter arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public MemberSummaryWriter getMemberSummaryWriter(ClassWriter arg0, int arg1)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public MemberSummaryWriter getMemberSummaryWriter(
			AnnotationTypeWriter arg0, int arg1) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public MethodWriter getMethodWriter(ClassWriter arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public PackageSummaryWriter getPackageSummaryWriter(PackageDoc arg0,
			PackageDoc arg1, PackageDoc arg2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public PropertyWriter getPropertyWriter(ClassWriter arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public SerializedFormWriter getSerializedFormWriter() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
