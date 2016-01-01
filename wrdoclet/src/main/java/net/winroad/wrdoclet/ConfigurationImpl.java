package net.winroad.wrdoclet;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.MessageRetriever;

/**
 * Configure the output based on the command line options.
 * <p>
 * Also determine the length of the command line option. For example,
 * </p>
 * @author Adams Lee
 */
public class ConfigurationImpl extends Configuration {

    private static ConfigurationImpl instance = new ConfigurationImpl();

    /**
     * The build date.  Note: For now, we will use
     * a version number instead of a date.
     */
    public static final String BUILD_DATE = System.getProperty("java.version");

    /**
     * The name of the constant values file.
     */
    public static final String CONSTANTS_FILE_NAME = "constant-values.html";

    /**
     * True if command line option "-noframe" is used. Default value is false.
     */
    public boolean noframe = false;
    
    /**
     * Argument for command line option "-dubboconfigpath".
     */
    public String dubboconfigpath = "";
    
    /**
     * Argument for command line option "-springcontextconfigpath".
     */
    public String springcontextconfigpath = "";
    
    /**
     * Argument for command line option "-excludedurlsxpath".
     */
    public String excludedurlsxpath = "";
    
    /**
     * Argument for command line option "-branchname".
     */
    public String branchname = "";
    
    /**
     * Argument for command line option "-systemname".
     */
    public String systemname = "";
    
    /**
     * Argument for command line option "-searchengine".
     */
    public String searchengine = "http://127.0.0.1:8080/solr/apidocs";
    
    /**
     * Unique Resource Handler for this package.
     */
    public final MessageRetriever standardmessage;

    /**
     * Constructor. Initialises resource for the
     * {@link com.sun.tools.doclets.MessageRetriever}.
     */
    private ConfigurationImpl() {
        standardmessage = new MessageRetriever(this,
            "com.sun.tools.doclets.formats.html.resources.standard");
    }

    /**
     * Reset to a fresh new ConfigurationImpl, to allow multiple invocations
     * of javadoc within a single VM. It would be better not to be using
     * static fields at all, but .... (sigh).
     */
    public static void reset() {
        instance = new ConfigurationImpl();
    }

    public static ConfigurationImpl getInstance() {
        return instance;
    }

	/**
     * Return the build date for the doclet.
     */
    public String getDocletSpecificBuildDate() {
        return BUILD_DATE;
    }

    /**
     * Depending upon the command line options provided by the user, set
     * configure the output generation environment.
     *
     * @param options The array of option names and values.
     */
    public void setSpecificDocletOptions(String[][] options) {
        for (int oi = 0; oi < options.length; ++oi) {
            String[] os = options[oi];
            String opt = os[0].toLowerCase();
            if (opt.equals("-charset")) {
                charset = os[1];
            }  else if (opt.equals("-noframe")) {
                noframe = true;
            } else if (opt.equals("-dubboconfigpath")) {
                dubboconfigpath = os[1];
            } else if (opt.equals("-springcontextconfigpath")) {
            	springcontextconfigpath = os[1];
            } else if (opt.equals("-excludedurlsxpath")) {
            	excludedurlsxpath = os[1];
            } else if (opt.equals("-systemname")) {
            	systemname = os[1];
            } else if (opt.equals("-branchname")) {
                branchname = os[1];
            } else if (opt.equals("-searchengine")) {
            	searchengine = os[1];
            } 
        }
        if (root.specifiedClasses().length > 0) {
            Map<String,PackageDoc> map = new HashMap<String,PackageDoc>();
            PackageDoc pd;
            ClassDoc[] classes = root.classes();
            for (int i = 0; i < classes.length; i++) {
                pd = classes[i].containingPackage();
                if(! map.containsKey(pd.name())) {
                    map.put(pd.name(), pd);
                }
            }
        }
    }

    /**
     * Returns the "length" of a given option. If an option takes no
     * arguments, its length is one. If it takes one argument, it's
     * length is two, and so on. This method is called by JavaDoc to
     * parse the options it does not recognize. It then calls
     * {@link #validOptions(String[][], DocErrorReporter)} to
     * validate them.
     * <b>Note:</b><br>
     * The options arrive as case-sensitive strings. For options that
     * are not case-sensitive, use toLowerCase() on the option string
     * before comparing it.
     * </blockquote>
     *
     * @return number of arguments + 1 for a option. Zero return means
     * option not known.  Negative value means error occurred.
     */
    public int optionLength(String option) {
        int result = -1;
        if ((result = super.optionLength(option)) > 0) {
            return result;
        }
        // otherwise look for the options we have added
        option = option.toLowerCase();
        if (option.equals("-nodeprecatedlist") ||
            option.equals("-noindex") ||
            option.equals("-notree") ||
            option.equals("-nohelp") ||
            option.equals("-splitindex") ||
            option.equals("-serialwarn") ||
            option.equals("-use") ||
            option.equals("-nonavbar") ||
            option.equals("-noframe") ||
            option.equals("-nooverview")) {
            return 1;
        } else if (option.equals("-help")) {
            System.out.println(getText("doclet.usage"));
            return 1;
        } else if (option.equals("-footer") ||
                   option.equals("-header") ||
                   option.equals("-packagesheader") ||
                   option.equals("-doctitle") ||
                   option.equals("-windowtitle") ||
                   option.equals("-top") ||
                   option.equals("-bottom") ||
                   option.equals("-helpfile") ||
                   option.equals("-stylesheetfile") ||
                   option.equals("-charset") ||
                   option.equals("-overview") ||
                   option.equals("-xdocrootparent") ||
                   option.equals("-dubboconfigpath") ||
                   option.equals("-springcontextconfigpath") ||
                   option.equals("-excludedurlsxpath") ||
                   option.equals("-systemname") ||
                   option.equals("-branchname") ||
                   option.equals("-searchengine")) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean validOptions(String options[][],
            DocErrorReporter reporter) {
        boolean helpfile = false;
        boolean nohelp = false;
        boolean overview = false;
        boolean nooverview = false;
        boolean splitindex = false;
        boolean noindex = false;
        boolean hasSystemName = false;
        boolean hasBranchName = false;
        // check shared options
        if (!generalValidOptions(options, reporter)) {
            return false;
        }
        // otherwise look at our options
        for (int oi = 0; oi < options.length; ++oi) {
            String[] os = options[oi];
            String opt = os[0].toLowerCase();
            if (opt.equals("-helpfile")) {
                if (nohelp == true) {
                    reporter.printError(getText("doclet.Option_conflict",
                        "-helpfile", "-nohelp"));
                    return false;
                }
                if (helpfile == true) {
                    reporter.printError(getText("doclet.Option_reuse",
                        "-helpfile"));
                    return false;
                }
                File help = new File(os[1]);
                if (!help.exists()) {
                    reporter.printError(getText("doclet.File_not_found", os[1]));
                    return false;
                }
                helpfile = true;
            } else  if (opt.equals("-nohelp")) {
                if (helpfile == true) {
                    reporter.printError(getText("doclet.Option_conflict",
                        "-nohelp", "-helpfile"));
                    return false;
                }
                nohelp = true;
            } else if (opt.equals("-dubboconfigpath")) {
                File dubboConfig = new File(os[1]);
                if (!dubboConfig.exists()) {
                    reporter.printError(getText("doclet.File_not_found", os[1]));
                    return false;
                }
            } else if (opt.equals("-springcontextconfigpath")) {
                File springContextConfigPath = new File(os[1]);
                if (!springContextConfigPath.exists()) {
                    reporter.printError(getText("doclet.File_not_found", os[1]));
                    return false;
                }
            } else if (opt.equals("-xdocrootparent")) {
                try {
                    new URL(os[1]);
                } catch (MalformedURLException e) {
                    reporter.printError(getText("doclet.MalformedURL", os[1]));
                    return false;
                }
            } else if (opt.equals("-overview")) {
                if (nooverview == true) {
                    reporter.printError(getText("doclet.Option_conflict",
                        "-overview", "-nooverview"));
                    return false;
                }
                if (overview == true) {
                    reporter.printError(getText("doclet.Option_reuse",
                        "-overview"));
                    return false;
                }
                overview = true;
            } else  if (opt.equals("-nooverview")) {
                if (overview == true) {
                    reporter.printError(getText("doclet.Option_conflict",
                        "-nooverview", "-overview"));
                    return false;
                }
                nooverview = true;
            } else if (opt.equals("-splitindex")) {
                if (noindex == true) {
                    reporter.printError(getText("doclet.Option_conflict",
                        "-splitindex", "-noindex"));
                    return false;
                }
                splitindex = true;
            } else if (opt.equals("-noindex")) {
                if (splitindex == true) {
                    reporter.printError(getText("doclet.Option_conflict",
                        "-noindex", "-splitindex"));
                    return false;
                }
                noindex = true;
            } else if (opt.equals("-systemname")) {
            	hasSystemName = true;
            } else if (opt.equals("-branchname")) {
            	hasBranchName = true;
            }
        }
        if(!hasBranchName) {
        	reporter.printError("No branchName specified!");
        	return false;
        }
        if(!hasSystemName) {
        	reporter.printError("No systemName specified!");
        	return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public MessageRetriever getDocletSpecificMsg() {
        return standardmessage;
    }

    protected ClassDoc getValidClass(ClassDoc[] classarr) {
        if (!nodeprecated) {
            return classarr[0];
        }
        for (int i = 0; i < classarr.length; i++) {
            if (classarr[i].tags("deprecated").length == 0) {
                return classarr[i];
            }
        }
        return null;
    }

    protected boolean checkForDeprecation(RootDoc root) {
        ClassDoc[] classarr = root.classes();
        for (int i = 0; i < classarr.length; i++) {
            if (isGeneratedDoc(classarr[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public WRWriterFactory getWriterFactory() {
        return new WriterFactoryImpl();
    }

    /**
     * {@inheritDoc}
     */
    public Comparator<ProgramElementDoc> getMemberComparator() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Locale getLocale() {
        if (root instanceof com.sun.tools.javadoc.RootDocImpl)
            return ((com.sun.tools.javadoc.RootDocImpl)root).getLocale();
        else
            return Locale.getDefault();
    }
}